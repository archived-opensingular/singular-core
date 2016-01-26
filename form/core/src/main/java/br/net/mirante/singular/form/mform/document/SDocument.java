package br.net.mirante.singular.form.mform.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTypes;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.MIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.ServiceRegistry.Pair;
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
public class SDocument {

    public static final String FILE_TEMPORARY_SERVICE   = "fileTemporary";
    public static final String FILE_PERSISTENCE_SERVICE = "filePersistence";

    private MInstancia root;

    private int lastId = 0;

    private MInstanceListeners instanceListeners;

    private DefaultServiceRegistry registry = new DefaultServiceRegistry();

    private Map<Integer, MIAnnotation> annotationMap = new HashMap<>();

    public SDocument() {}

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
    final public Integer nextId() {
        if (lastId == -1) {
            return null;
        }
        return ++lastId;
    }

    public void setAttachmentPersistenceHandler(ServiceRef<IAttachmentPersistenceHandler> ref) {
        bindLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class, ref);
    }

    public IAttachmentPersistenceHandler getAttachmentPersistenceHandler() {
        IAttachmentPersistenceHandler ref = lookupService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        if (ref == null) {
            ref = new InMemoryAttachmentPersitenceHandler();
            bindLocalService(FILE_TEMPORARY_SERVICE,
                IAttachmentPersistenceHandler.class, ServiceRef.of(ref));
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

    public final void setRoot(MInstancia root) {
        if (this.root != null) {
            throw new SingularFormException("Não é permitido altera o raiz depois que o mesmo for diferente de null");
        }
        this.root = Objects.requireNonNull(root);
        MTypes.streamDescendants(getRoot().getMTipo(), true).forEach(tipo -> {
            // init dependencies
            final Supplier<Collection<MTipo<?>>> func = tipo.getValorAtributo(MPacoteBasic.ATR_DEPENDS_ON_FUNCTION);
            if (func != null) {
                for (MTipo<?> dependency : func.get())
                    dependency.getDependentTypes().add(tipo);
            }
        });
    }

    /**
     * Stablishes a new registry where to look for services, which is chained
     *  to the default one.
     */
    public void addServiceRegistry(ServiceRegistry registry) {
        this.registry.addRegistry(registry);
    }

    /**
     * USO INTERNO APENAS. Retorna os serviços registrados diretamente no
     * documento.
     *
     * @see ServiceRegistry#services()
     */
    public Map<String, Pair> getLocalServices() {
        return registry.services();
    }

    /**
     * Tenta encontrar um serviço da classe solicitada supondo que o nome no
     * registro é o nome da própria classe.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupService(Class<T> targetClass) {
        return registry.lookupService(targetClass);
    }

    /**
     * Tenta encontrar um serviço registrado com o nome informado. Se o
     * resultado não for null e não implementar a classe solicitada, dispara
     * exception.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupService(String name, Class<T> targetClass) {
        return registry.lookupService(name, targetClass);
    }

    /**
     * Tenta encontrar um serviço registrado <u>diretamente no documento</u> com
     * o nome informado. Se o resultado não for null e não implementar a classe
     * solicitada, dispara exception.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupLocalService(String name, Class<T> targetClass) {
        return registry.lookupLocalService(name, targetClass);
    }

    /**
     * Registar um serviço com o nome da classe informada. O provider pode ser
     * uma classe derivada da registerClass.
     */
    public <T> void bindLocalService(Class<T> registerClass, ServiceRef<? extends T> provider) {
        registry.bindLocalService(registerClass, provider);
    }

    /**
     * Registar um serviço com o nome informado.
     */
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, ServiceRef<? extends T> provider) {
        registry.bindLocalService(serviceName, registerClass, provider);
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
                instance.updateExists();
                instance.updateObrigatorio();
                MInstances.updateBooleanAttribute(instance, MPacoteBasic.ATR_ENABLED, MPacoteBasic.ATR_ENABLED_FUNCTION);
                MInstances.updateBooleanAttribute(instance, MPacoteBasic.ATR_VISIVEL, MPacoteBasic.ATR_VISIBLE_FUNCTION);
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
        IAttachmentPersistenceHandler persistent = lookupService(
            SDocument.FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
        IAttachmentPersistenceHandler temporary = getAttachmentPersistenceHandler();
        new AttachmentPersistenceHelper(temporary, persistent).doPersistence(root);
    }

    public MIAnnotation annotation(Integer id) {  return annotationMap.get(id);  }
    public void annotation(Integer id, MIAnnotation annotation) {   this.annotationMap.put(id, annotation);}
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
