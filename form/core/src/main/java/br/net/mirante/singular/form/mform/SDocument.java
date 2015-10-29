package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.InMemoryAttachmentPersitenceHandler;

/**
 * <p>
 * Representa um instância de formulário e seus anexos (se houver), podendo ser
 * usado para consultas, edições ou persistência.
 * </p>
 * <p>
 * O Document foi construido para ser serializável de modo a facilitar situações
 * em que seja necessário devido a integração com frameworks Web.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class SDocument implements Serializable {

    private MInstancia root;

    private Map<String, ServiceRef<?>> services;

    private ServiceRef<IAttachmentPersistenceHandler> attachmentPersistenceHandlerRef;

    SDocument() {
    }

    // SDocument(MInstancia root) {
    // this.root = Objects.requireNonNull(root);
    // }

    public void setAttachmentPersistenceHandler(ServiceRef<IAttachmentPersistenceHandler> ref) {
        attachmentPersistenceHandlerRef = ref;
    }

    public IAttachmentPersistenceHandler getAttachmentPersistenceHandler() {
        if (attachmentPersistenceHandlerRef == null) {
            attachmentPersistenceHandlerRef = ServiceRef.of(new InMemoryAttachmentPersitenceHandler());
        }
        return attachmentPersistenceHandlerRef.get();
    }

    public MInstancia getRoot() {
        if (root == null) {
            throw new RuntimeException("Instancia raiz não foi configurada");
        }
        return root;
    }

    final void setRoot(MInstancia root) {
        if (this.root != null) {
            throw new RuntimeException("Não é permitido altera o raiz depois que o mesmo for diferente de null");
        }
        this.root = Objects.requireNonNull(root);
    }

    /**
     * Tenta encontrar um serviço da classe solicitada supondo que o nome no
     * registro é o nome da própria classe.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupLocalService(Class<T> targetClass) {
        return lookupLocalService(targetClass.getName(), targetClass);
    }

    /**
     * Tenta encontrar um serviço registrado com o nome informado. Se o
     * resultado náo for null e náo implementar a classe solicitada, dispara
     * exception.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupLocalService(String name, Class<T> targetClass) {
        if (services != null) {
            ServiceRef<?> ref = services.get(name);
            if (ref != null) {
                Object value = ref.get();
                if (value == null) {
                    services.remove(name);
                } else if(! targetClass.isInstance(value)) {
                    throw new SingularFormException("Para o serviço '" + name + "' foi encontrado um valor da classe "
                            + value.getClass().getName() + " em vez da classe esperada " + targetClass.getName());
                } else {
                    return targetClass.cast(value);
                }
            }
        }
        return null;
    }

    /**
     * Registar um serviço com o nome da classe informada. O provider pode ser
     * uma classe derivada da registerClass.
     */
    public <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider) {
        bindLocalService(registerClass.getName(), provider);
    }

    /**
     * Registar um serviço com o nome informado.
     */
    public void bindLocalService(String serviceName, ServiceRef<?> provider) {
        if (services == null) {
            services = new HashMap<>();
        }
        services.put(Objects.requireNonNull(serviceName), Objects.requireNonNull(provider));
    }

}
