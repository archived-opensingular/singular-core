package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MDicionarioLoader;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.SDocument;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.util.xml.MElement;

/**
 * <p>
 * Classe de suporte a serialização e deserialização de
 * {@link br.net.mirante.singular.form.mform.SDocument} e
 * {@link br.net.mirante.singular.form.mform.MInstancia}.
 * <p>
 * <p>
 * Uso muito comum são nas interface de edição des formulários.
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
    public static FormSerialized toSerializedObject(MInstancia instance) {
        FormSerialized fs = toSerialized(instance.getDocument());
        if (instance.getDocument().getRoot() != instance) {
            fs.setFocusFieldPath(instance.getPathFromRoot());
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
    private static FormSerialized toSerialized(SDocument document) {
        MElement xml = MformPersistenciaXML.toXML(document.getRoot());
        FormSerialized fs = new FormSerialized(document.getRoot().getMTipo().getNome(), xml);
        Map<String, ServiceRef<?>> services = document.getLocalServices();
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
     * originalmente serializado um documento, então retorna a instânci raiz do
     * documento.
     *
     * @param fs
     *            Dado a ser deserializado
     * @param dicionaryLoader
     *            Fornece as definições dos tipos (dicionário) para ser usado no
     *            contexto da recuperação.
     * @return Sepre diferente de Null
     */
    public static MInstancia toInstance(FormSerialized fs, MDicionarioLoader dicionaryLoader) {
        try {
            MTipo<?> rootType = dicionaryLoader.loadType(fs.getRootType());
            MInstancia root = MformPersistenciaXML.fromXML(rootType, fs.getXml());
            if (fs.getServices() != null) {
                SDocument document = root.getDocument();
                fs.getServices().entrySet().stream().forEach(entry -> document.bindLocalService(entry.getKey(), entry.getValue()));
            }

            if (StringUtils.isBlank(fs.getFocusFieldPath())) {
                return root;
            }
            return ((ICompositeInstance) root).getCampo(fs.getFocusFieldPath());
        } catch (Exception e) {
            String msg = "Erro deseriazando " + fs.getRootType();
            if (!StringUtils.isBlank(fs.getFocusFieldPath())) {
                msg += " com subPath '" + fs.getRootType() + '\'';
            }
            throw new SingularFormException(msg + ": " + e.getMessage(), e);
        }
    }

    /**
     * Objeto transitório para guardar uma versão serilizável de MInstance ou
     * MDocument.
     *
     * @author Daniel C. Bordin
     */
    public static final class FormSerialized implements Serializable {

        private final String rootType;
        private final MElement xml;
        private String focusFieldPath;
        private Map<String, ServiceRef<?>> services;

        public FormSerialized(String rootType, MElement xml) {
            this.rootType = rootType;
            this.xml = xml;
        }

        public String getRootType() {
            return rootType;
        }

        public String getFocusFieldPath() {
            return focusFieldPath;
        }

        public MElement getXml() {
            return xml;
        }

        public void setFocusFieldPath(String focusFieldPath) {
            this.focusFieldPath = focusFieldPath;
        }

        public Map<String, ServiceRef<?>> getServices() {
            return services;
        }

        public void setServices(Map<String, ServiceRef<?>> services) {
            this.services = services;
        }

    }
}
