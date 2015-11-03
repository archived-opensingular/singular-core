package br.net.mirante.singular.form.mform;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

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

    SDocument() {
    }

    public void setAttachmentPersistenceHandler(ServiceRef<IAttachmentPersistenceHandler> ref) {
        bindLocalService(IAttachmentPersistenceHandler.class, ref);
    }

    public IAttachmentPersistenceHandler getAttachmentPersistenceHandler() {
        IAttachmentPersistenceHandler ref = lookupLocalService(IAttachmentPersistenceHandler.class);
        if (ref == null) {
            ref = new InMemoryAttachmentPersitenceHandler();
            bindLocalService(IAttachmentPersistenceHandler.class, ServiceRef.of(ref));
        }
        return ref;
    }

    /**
     * Obtêm a instância que representa o documento com um todo.
     */
    public MInstancia getRoot() {
        if (root == null) {
            throw new SingularFormException("Instancia raiz não foi configurada");
        }
        return root;
    }

    final void setRoot(MInstancia root) {
        if (this.root != null) {
            throw new SingularFormException("Não é permitido altera o raiz depois que o mesmo for diferente de null");
        }
        this.root = Objects.requireNonNull(root);
    }

    public Map<String, ServiceRef<?>> getLocalServices() {
        return (services == null) ? Collections.emptyMap() : ImmutableMap.copyOf(services);
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
     * Tenta encontrar um serviço da classe solicitada supondo que o nome no
     * registro é o nome da própria classe junto com o subName. Ou seja, retorna
     * um caso específico de targetClass.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupLocalService(Class<T> targetClass, String subName) {
        return lookupLocalService(toLookupName(targetClass, subName), targetClass);
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
     * Registar um serviço com o nome da classe informada mais o subNome. O
     * provider pode ser uma classe derivada da registerClass.
     */
    public <T> void bindLocalService(Class<T> registerClass, String subName, ServiceRef<? extends T> provider) {
        bindLocalService(toLookupName(registerClass, subName), provider);
    }

    private static <T> String toLookupName(Class<T> registerClass, String subName) {
        return registerClass.getName() + ":" + Objects.requireNonNull(subName);
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
