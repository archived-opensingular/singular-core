package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * <p>
 * Classe de suporte a serialização e deserialização de
 * {@link br.net.mirante.singular.form.mform.document.SDocument} e
 * {@link SInstance}.
 * </p>
 * <p>
 * Tendo em vista que as definições de tipos não são serializadas (vão apenas os
 * dados das instâncias), ao deserealizar é necessário recuperar a definição do
 * tipo no correto dicionário.
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

        checkIfSerializable(root);
        FormSerialized fs = new FormSerialized(document.getRootRefType().get(), root.getType().getName(), xml, annotations,
                document.getDocumentFactoryRef());
        serializeServices(document, fs);
        return fs;
    }

    /**
     * Verifica se a instância atende os critérios necessários para ser
     * serializável. Para tanto é necessário que tenha sido criado a partir de
     * um {@link SDocumentFactory} e com o uso {@link RefType}.
     *
     * @throws SingularFormException
     *             Se não atender os critérios
     */
    public final static void checkIfSerializable(SInstance instance) {
        SDocument document = instance.getDocument();
        if (!document.getRootRefType().isPresent()) {
            throw new SingularFormException("Não foi configurado o rootRefType no Document da instância, o que impedirá a "
                    + "serialização/deserialização do mesmo. " + "A instância deve ser criada usando " + SDocumentFactory.class.getName(),
                    instance);
        }
        if(document.getDocumentFactoryRef() == null) {
            throw new SingularFormException("Não foi configurado o DocumentFactory no Document da instância, o que impedirá a "
                    + "serialização/deserialização do mesmo. " + "A instância deve ser criada usando " + SDocumentFactory.class.getName(),
                    instance);
        }
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
            SInstance root = MformPersistenciaXML.fromXML(fs.getRefRootType(), fs.getXml(), fs.getSDocumentFactoryRef().get());
            deserializeServices(fs.getServices(), root.getDocument());
            MformPersistenciaXML.annotationLoadFromXml(root, fs.getAnnotations());
            return defineRoot(fs, root);
        } catch (Exception e) {
            throw deserializingError(fs, e);
        }
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
        String msg = "Error when deserializing " + fs.getRootTypeName();
        if (!StringUtils.isBlank(fs.getFocusFieldPath())) {
            msg += " with subPath '" + fs.getFocusFieldPath() + '\'';
        }
        return new SingularFormException(msg, e);
    }

}

