package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.MTipoAnnotationList;
import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * <p>
 * Classe de suporte a serialização e deserialização de
 * {@link br.net.mirante.singular.form.mform.document.SDocument} e
 * {@link br.net.mirante.singular.form.mform.MInstancia}.
 * </p>
 * <p>
 * Tendo em vista que as definições de tipos não serializadas (vão apenas os
 * dados das instâncias), ao deserealizar é necessário ter o dicionário com as
 * definições dos tipos. Há três formas de resolver a questão:
 * <ul>
 * <li>Setar o MDicionarioResolver default (singleton) em
 * {@link MDicionarioResolver#setDefault(MDicionarioResolver)}</li>
 * <li>Ao deserializar informar MDicionarioResolver a ser utilizado:
 * {@link #toInstance(FormSerialized, MDicionarioResolver)}</li>
 * <li>Ao gerar a versão serialização, passar um
 * {@link MDicionarioResolverSerializable} para também ser serializado junto com
 * os dados. Na volta (deserialização) usa esse resolver que foi serializado
 * junto com os dados:
 * </ul >
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class FormSerializationUtil {

    private static DictionaryCache dictionaries = new DictionaryCache();

    private FormSerializationUtil() {
    }

    /**
     * <p>
     * Gera uma vesão serializável da instancia. Implica em serializar todo o
     * documento associado a instância, contudo guarda o path da instancia alvo
     * para poder recuperar corretamente depois.
     * </p>
     * <p>
     * Não serializa a definição do tipo (dicionário). Guarda apenas o nome do
     * tipo.
     * </p>
     */
    public static FormSerialized toSerializedObject(MInstancia instance) {
        return toSerializedObject(instance, null);
    }

    /**
     * <p>
     * Gera uma vesão serializável da instancia incluindo com resolvedor para
     * recuperação do dicionário quando a instância for deserializada. Implica
     * em serializar todo o documento associado a instância, contudo guarda o
     * path da instancia alvo para poder recuperar corretamente depois.
     * </p>
     * <p>
     * Não serializa a definição do tipo (dicionário). Guarda apenas o nome do
     * tipo.
     *
     * @param dicionarioResolverSerializable
     *            Pode ser null. Se for passado também serializa o dicionário
     *            resolver para facilitar a recuperação.
     *            </p>
     */
    public static FormSerialized toSerializedObject(MInstancia instance,
                                                    MDicionarioResolverSerializable dicionarioResolverSerializable) {
        FormSerialized fs = toSerialized(instance.getDocument(), dicionarioResolverSerializable);
        defineFocusField(instance, fs);
        setDictionaryIfAny(instance, dicionarioResolverSerializable, fs);
        return fs;
    }

    private static void defineFocusField(MInstancia instance, FormSerialized fs) {
        if (instance.getDocument().getRoot() != instance) {
            fs.setFocusFieldPath(instance.getPathFromRoot());
        }
    }

    private static void setDictionaryIfAny(MInstancia instance, MDicionarioResolverSerializable dicionarioResolverSerializable, FormSerialized fs) {
        if (dicionarioResolverSerializable == null) {
            fs.setDictionaryId(dictionaries.put(instance.getDicionario()));
        }
    }

    /**
     * <p>
     * Gera uma vesão serializável do documento.
     * </p>
     * <p>
     * Não serializa a definição do tipo (dicionário). Guarda apenas o nome do
     * tipo.
     * </p>
     */
    private static FormSerialized toSerialized(SDocument document,
                                  MDicionarioResolverSerializable dicionaroResolverSerializable) {
        MInstancia root = document.getRoot();
        MElement xml = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root);
        MElement annotations = null;
        if(root.as(AtrAnnotation::new).hasAnnotation()){
            annotations = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root.as(AtrAnnotation::new).persistentAnnotations());
        }
        FormSerialized fs = new FormSerialized(root.getMTipo().getNome(), xml, annotations,
                                                dicionaroResolverSerializable);
        serializeServices(document, fs);
        return fs;
    }

    private static void serializeServices(SDocument document, FormSerialized fs) {
        Map<String, Pair> services = document.getLocalServices();
        if (!services.isEmpty()) {
            if (!(services instanceof Serializable)) {
                throw new SingularFormException("The Document service map is not Serializable.");
            }
            fs.setServices(services);
        }
    }

    /**
     * Recupera a instância e o documento que foi serializado. Se foi
     * originalmente serializado um documento, então retorna a instância raiz do
     * documento. Se foi serialziado um sub parte do documento, retorna a
     * instancia da sub parte, mas na prática deserializa todo o documento.
     *
     * @param fs
     *            Dado a ser deserializado
     * @return Sempre diferente de Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou tipo necessário.
     */
    public static MInstancia toInstance(FormSerialized fs) {
        return toInstance(fs, null);
    }

    /**
     * Recupera a instância e o documento que foi serializado. Se foi
     * originalmente serializado um documento, então retorna a instância raiz do
     * documento. Se foi serialziado um sub parte do documento, retorna a
     * instancia da sub parte, mas na prática deserializa todo o documento.
     *
     * @param fs
     *            Dado a ser deserializado
     * @param dictionaryResolver
     *            Fornece as definições dos tipos (dicionário) para ser usado no
     *            contexto da recuperação. Senão for informado, tenta usar a
     *            versão serializada ou versão default
     *            {@link MDicionarioResolver#getDefault()}
     * @return Sempre diferente de Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou tipo necessário.
     */
    public static MInstancia toInstance(FormSerialized fs, MDicionarioResolver dictionaryResolver) {
        try {
            MTipo<?> rootType = loaType(fs, dictionaryResolver, fs.getRootType());
            MInstancia root = MformPersistenciaXML.fromXML(rootType, fs.getXml());

            deserializeServices(fs.getServices(), root.getDocument());
            if(fs.getAnnotations() != null){
                MTipo<?> annotationsList = loaType(fs, dictionaryResolver, MPacoteCore.NOME+"."+MTipoAnnotationList.NAME);
                MILista persisted = (MILista) MformPersistenciaXML.fromXML(annotationsList, fs.getAnnotations());
                root.as(AtrAnnotation::new).loadAnnotations(persisted);
            }
            return defineRoot(fs, root);
        } catch (Exception e) {
            throw deserializingError(fs, e);
        }
    }

    private static MTipo<?> loaType(FormSerialized fs, MDicionarioResolver dicionaryResolver, String rootType) {
        dicionaryResolver = (dicionaryResolver != null) ? dicionaryResolver : fs.getDicionarioResolver();
        if (dicionaryResolver == null) {
            if(fs.getDictionaryId() != null && dictionaries.has(fs.getDictionaryId())){
                return dictionaries.get(fs.getDictionaryId()).getTipo(rootType);
            }else{
                dicionaryResolver = MDicionarioResolver.getDefault();
            }
        }
        return dicionaryResolver.loadType(rootType);
    }

    private static void deserializeServices(Map<String, Pair> services, SDocument document) {
        if (services != null) {
            services.entrySet().stream().forEach(entry -> bindService(document, entry));
        }
    }

    private static void bindService(SDocument document, Map.Entry<String, Pair> entry) {
        Pair p = entry.getValue();
        document.bindLocalService(entry.getKey(), (Class<Object>) p.type, p.provider);
    }

    private static MInstancia defineRoot(FormSerialized fs, MInstancia root) {
        if (StringUtils.isBlank(fs.getFocusFieldPath())) { return root; }
        return ((ICompositeInstance) root).getCampo(fs.getFocusFieldPath());
    }

    private static SingularFormException deserializingError(FormSerialized fs, Exception e) {
        String msg = "Error when deserializing " + fs.getRootType();
        if (!StringUtils.isBlank(fs.getFocusFieldPath())) {
            msg += " with subPath '" + fs.getRootType() + '\'';
        }
        return new SingularFormException(msg, e);
    }

}

