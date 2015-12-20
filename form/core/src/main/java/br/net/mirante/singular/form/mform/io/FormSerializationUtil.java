package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.net.mirante.singular.form.mform.*;
import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;
import br.net.mirante.singular.form.util.xml.MElement;

import static com.google.common.collect.Maps.newHashMap;

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
 * {@link #toInstance(FormSerialized, MDicionarioResolverSerializable)}</li>
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
     * @param dicionarioResolverSerializable Pode ser null. Se for passado também serializa o dicionário
     *                                       resolver para facilitar a recuperação.
     *                                       </p>
     */
    public static FormSerialized toSerializedObject(MInstancia instance,
                                                    MDicionarioResolverSerializable dicionarioResolverSerializable) {
        FormSerialized fs = toSerialized(instance.getDocument(), dicionarioResolverSerializable);
        if (instance.getDocument().getRoot() != instance) {
            fs.setFocusFieldPath(instance.getPathFromRoot());
        }
        if (dicionarioResolverSerializable == null) {
            fs.setDictionaryId(dictionaries.put(instance.getDicionario()));
        }
        return fs;
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
        MElement xml = MformPersistenciaXML.toXMLPreservingRuntimeEdition(document.getRoot());
        FormSerialized fs = new FormSerialized(document.getRoot().getMTipo().getNome(), xml, dicionaroResolverSerializable);
        Map<String, Pair> services = document.getServices();
        if (!services.isEmpty()) {
            if (!(services instanceof Serializable)) {
                throw new SingularFormException("O mapa de serviço do document não é serializável");
            }
            fs.setServices(services);
        }
        return fs;
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
     * @param dicionaryLoader
     *            Fornece as definições dos tipos (dicionário) para ser usado no
     *            contexto da recuperação. Senão for informado, tenta usar a
     *            versão serializada ou versão default
     *            {@link MDicionarioResolver#getDefault()}
     * @return Sempre diferente de Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou tipo necessário.
     */
    public static MInstancia toInstance(FormSerialized fs, MDicionarioResolver dicionaryResolver) {
        try {
            MTipo<?> rootType = null;
                    dicionaryResolver = (dicionaryResolver != null) ? dicionaryResolver : fs.getDicionarioResolver();
            if (dicionaryResolver == null) {
                if(fs.getDictionaryId() != null && dictionaries.has(fs.getDictionaryId())){
                    rootType = dictionaries.get(fs.getDictionaryId()).getTipo(fs.getRootType());
                }else{
                    dicionaryResolver = MDicionarioResolver.getDefault();
                }
            }
            if(rootType == null){
                rootType = dicionaryResolver.loadType(fs.getRootType());
            }
            MInstancia root = MformPersistenciaXML.fromXML(rootType, fs.getXml());
            if (fs.getServices() != null) {
                SDocument document = root.getDocument();
                fs.getServices().entrySet().stream()
                    .forEach(entry -> {
                        Pair p = entry.getValue();
                        document.bindLocalService(entry.getKey(), p.type, p.provider);
                        });
            }

            if (StringUtils.isBlank(fs.getFocusFieldPath())) {
                return root;
            }
            return ((ICompositeInstance) root).getCampo(fs.getFocusFieldPath());
        } catch (Exception e) {
            String msg = "Error when deserializing " + fs.getRootType();
            if (!StringUtils.isBlank(fs.getFocusFieldPath())) {
                msg += " with subPath '" + fs.getRootType() + '\'';
            }
            throw new SingularFormException(msg + ": " + e.getMessage(), e);
        }
    }

}

