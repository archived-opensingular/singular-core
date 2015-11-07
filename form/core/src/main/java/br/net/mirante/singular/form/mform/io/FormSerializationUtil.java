package br.net.mirante.singular.form.mform.io;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MDicionarioResolver;
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
 * <li>Ao gerar a versão serialização, passar um MDicionarioResolver
 * serializável para também ser serializado junto com os dados. Na volta usa
 * esse resolver que foi serializado junto com os dados:
 * {@link #toInstance(FormSerialized, MDicionarioResolver)}</li>
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
     * @param dicionaroResolverSerializable
     *            Pode ser null. Se for passado também serializa o dicionário
     *            resolver para facilitar a recuperação.
     *            </p>
     */
    public static <DR extends MDicionarioResolver & Serializable> FormSerialized toSerializedObject(MInstancia instance,
            DR dicionaroResolverSerializable) {
        FormSerialized fs = toSerialized(instance.getDocument(), dicionaroResolverSerializable);
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
    private static <DR extends MDicionarioResolver & Serializable> FormSerialized toSerialized(SDocument document,
            DR dicionaroResolverSerializable) {
        MElement xml = MformPersistenciaXML.toXMLPreservingRuntimeEdition(document.getRoot());
        FormSerialized fs = new FormSerialized(document.getRoot().getMTipo().getNome(), xml, dicionaroResolverSerializable);
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
            dicionaryResolver = (dicionaryResolver != null) ? dicionaryResolver : fs.getDicionarioResolver();
            if (dicionaryResolver == null) {
                dicionaryResolver = MDicionarioResolver.getDefault();
            }
            MTipo<?> rootType = dicionaryResolver.loadType(fs.getRootType());
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

        private final MDicionarioResolver dicionarioResolver;
        private final String rootType;
        private final MElement xml;
        private String focusFieldPath;
        private Map<String, ServiceRef<?>> services;

        public <DR extends MDicionarioResolver & Serializable> FormSerialized(String rootType, MElement xml, DR dicionarioResolver) {
            this.dicionarioResolver = dicionarioResolver;
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

        public MDicionarioResolver getDicionarioResolver() {
            return dicionarioResolver;
        }

    }
}
