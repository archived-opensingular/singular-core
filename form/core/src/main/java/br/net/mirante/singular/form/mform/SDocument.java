package br.net.mirante.singular.form.mform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.MInstanceEventType;
import br.net.mirante.singular.form.mform.event.MInstanceListeners;

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
@SuppressWarnings("serial")
public class SDocument {

    public static final String FILE_PERSISTENCE_SERVICE = "filePersistence";

    private MInstancia root;

    private Map<String, ServiceRef<?>> services;

    private int lastId = 0;

    private MInstanceListeners instanceListeners;

    SDocument() {}

    /**
     * Contador interno para IDs de instancia. É preservado entre peristência
     * para garantir que um ID nunca seja reutilziado dentro de um mesmo
     * documento.
     */
    public final int getLastId() {
        return lastId;
    }

    /**
     * Apenas para uso interno de soluções de persistência de documentos. Não
     * usar sem ser para esse fim.
     */
    public final void setLastId(int value) {
        lastId = value;
    }

    /** Retorna null se estiver no modo de restore da persistencia. */
    final Integer nextId() {
        if (lastId == -1) {
            return null;
        }
        return ++lastId;
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
                } else if (!targetClass.isInstance(value)) {
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

    public MInstanceListeners getInstanceListeners() {
        if (this.instanceListeners == null)
            this.instanceListeners = new MInstanceListeners();
        return this.instanceListeners;
    }

    /**
     * 
     * @return eventos coletados
     */
    public void updateAttributes(IMInstanceListener listener) {
        if (listener != null)
            getInstanceListeners().add(MInstanceEventType.ATTRIBUTE_CHANGED, listener);

        try {
            MInstances.visitAll(getRoot(), true, instance -> {
                Predicate<MInstancia> requiredFunc = instance.getValorAtributo(MPacoteCore.ATR_OBRIGATORIO_FUNCTION);
                if (requiredFunc != null)
                    instance.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, requiredFunc.test(instance));

                Predicate<MInstancia> enabledFunc = (Predicate<MInstancia>) instance.getValorAtributo(MPacoteBasic.ATR_ENABLED_FUNCTION.getNomeCompleto());
                if (enabledFunc != null)
                    instance.setValorAtributo(MPacoteBasic.ATR_ENABLED, enabledFunc.test(instance));

                Predicate<MInstancia> visibleFunc = (Predicate<MInstancia>) instance.getValorAtributo(MPacoteBasic.ATR_VISIBLE_FUNCTION.getNomeCompleto());
                if (visibleFunc != null)
                    instance.setValorAtributo(MPacoteBasic.ATR_VISIVEL, visibleFunc.test(instance));
            });
        } finally {
            getInstanceListeners().remove(MInstanceEventType.ATTRIBUTE_CHANGED, listener);
        }
    }

    //TODO: Review how this method works. It'd be better if the developer did 
    //  not had to remember to call this before saving in the database.
    //  Maybe if the document worked as an Active Record, we'd be able to
    //  intercept the persist call and do this job before the model
    //  would be persisted.
    public void persistFiles() {
        IAttachmentPersistenceHandler persistent = lookupLocalService(
            SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
        IAttachmentPersistenceHandler temporary = getAttachmentPersistenceHandler();
        new AttachmentPersistenceHelper(temporary, persistent).doPersistence(root);
    }

}

/**
 * Responsible for moving files from temporary state to persistent.
 * 
 * @author Fabricio Buzeto
 *
 */
class AttachmentPersistenceHelper {

    private IAttachmentPersistenceHandler temporary, persistent;

    public AttachmentPersistenceHelper(IAttachmentPersistenceHandler temporary,
        IAttachmentPersistenceHandler persistent) {
        this.temporary = temporary;
        this.persistent = persistent;
    }

    public void doPersistence(MInstancia element) {
        if (element instanceof MIAttachment) {
            handleAttachment((MIAttachment) element);
        } else if (element instanceof ICompositeInstance) {
            visitChildrenIfAny((ICompositeInstance) element);
        }
    }

    private void handleAttachment(MIAttachment attachment) {
        moveFromTemporaryToPersistentIfNeeded(attachment);
    }

    private void moveFromTemporaryToPersistentIfNeeded(MIAttachment attachment) {
        if (!Objects.equals(attachment.getFileId(), attachment.getOriginalFileId())) {
            IAttachmentRef fileRef = temporary.getAttachment(attachment.getFileId());
            if (fileRef != null) {
                IAttachmentRef newRef = persistent.addAttachment(fileRef.getContentAsByteArray());
                deleteOldFiles(attachment, fileRef);
                updateFileId(attachment, newRef);
            }
        }
    }

    private void deleteOldFiles(MIAttachment attachment, IAttachmentRef fileRef) {
        temporary.deleteAttachment(fileRef.getId());
        persistent.deleteAttachment(attachment.getOriginalFileId());
    }

    private void updateFileId(MIAttachment attachment, IAttachmentRef newRef) {
        attachment.setFileId(newRef.getId());
        attachment.setOriginalFileId(newRef.getId());
    }

    private void visitChildrenIfAny(ICompositeInstance composite) {
        if (!composite.getAllChildren().isEmpty()) {
            visitAllChildren(composite);
        }
    }

    private void visitAllChildren(ICompositeInstance composite) {
        for (MInstancia child : composite.getAllChildren()) {
            doPersistence(child);
        }
    }
}
