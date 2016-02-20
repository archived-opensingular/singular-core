package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SDictionaryLoader;
import br.net.mirante.singular.form.mform.RefSDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * <p>
 * Classe de suporte a serialização e deserialização de
 * {@link br.net.mirante.singular.form.mform.document.SDocument} e
 * {@link SInstance}.
 * </p>
 * <p>
 * Tendo em vista que as definições de tipos não serializadas (vão apenas os
 * dados das instâncias), ao deserealizar é necessário ter o dicionário com as
 * definições dos tipos. Há três formas de resolver a questão:
 * <ul>
 * <li>Setar o MDicionarioResolver default (singleton) em
 * {@link SDictionaryLoader#setDefault(SDictionaryLoader)}</li>
 * </ul >
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class FormSerializationUtil {

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
    public static FormSerialized toSerializedObject(SInstance instance) {
        FormSerialized fs = toSerialized(instance.getDocument());
        defineFocusField(instance, fs);
        return fs;
    }

    private static void defineFocusField(SInstance instance, FormSerialized fs) {
        if (instance.getDocument().getRoot() != instance) {
            fs.setFocusFieldPath(instance.getPathFromRoot());
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
    private static FormSerialized toSerialized(SDocument document) {
        SInstance root = document.getRoot();
        MElement xml = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root);
        MElement annotations = null;
        if(root.as(AtrAnnotation::new).hasAnnotation()){
            annotations = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root.as(AtrAnnotation::new).persistentAnnotations());
        }

        RefSDictionary dicionaryRef = verificarDicionaryRef(root);
        FormSerialized fs = new FormSerialized(root.getType().getName(), xml, annotations, dicionaryRef,
                root.getDocument().getDocumentFactoryRef());
        serializeServices(document, fs);
        return fs;
    }

    final static RefSDictionary verificarDicionaryRef(SInstance instance) {
        return verificarDicionaryRef(instance.getDictionary(), instance);
    }

    private final static RefSDictionary verificarDicionaryRef(SDictionary dicionary, SInstance instance) {
        Optional<RefSDictionary> dicionaryRef = dicionary.getSerializableDictionarySelfReference();
        if (! dicionaryRef.isPresent()) {
            throw new SingularFormException("Não foi configurado o dicionaryRef no dicionário da instância, o que impedirá a "
                    + "serialização/deserialização do mesmo.(ver " + RefSDictionary.class.getName()
                    + " e SDicionary.setSerializableDictionarySelfReference()).", instance);
        }
        return dicionaryRef.get();
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
    public static SInstance toInstance(FormSerialized fs) {
        try {
            SType<?> rootType = loadType(fs, fs.getRootType());
            SInstance root = MformPersistenciaXML.fromXML(rootType, fs.getXml());
            if (fs.getsDocumentFactoryRef() != null) {
                root.getDocument().setDocumentFactory(fs.getsDocumentFactoryRef().get());
            }

            deserializeServices(fs.getServices(), root.getDocument());
            MformPersistenciaXML.annotationLoadFromXml(root, fs.getAnnotations());
            return defineRoot(fs, root);
        } catch (Exception e) {
            throw deserializingError(fs, e);
        }
    }

    private static SType<?> loadType(FormSerialized fs, String rootType) {
        if (fs.getDictionaryRef() == null) {
            throw new SingularFormException("O DicionaryRef não foi serializado");
        }
        SDictionary d = fs.getDictionaryRef().get();
        if (d == null) {
            throw new SingularFormException(
                    "O DicionaryRef '" + fs.getDictionaryRef().getClass().getName() + "' retornou null para o dicionário");
        }
        return d.getType(rootType);
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

    private static SInstance defineRoot(FormSerialized fs, SInstance root) {
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

