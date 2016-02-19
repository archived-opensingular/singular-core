package br.net.mirante.singular.form.mform.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MTypes;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
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

    private SInstance root;

    private int lastId = 0;

    private MInstanceListeners instanceListeners;

    private DefaultServiceRegistry registry = new DefaultServiceRegistry();

    private Map<Integer, SIAnnotation> annotationMap = new HashMap<>();

    private SDocumentFactory documentFactory;

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

    /**
     * Registra a persistência temporária de anexos. A persistência temporária
     * guarda os anexos em quanto o documento não é saldo.
     */
    public void setAttachmentPersistenceTemporaryHandler(ServiceRef<IAttachmentPersistenceHandler> ref) {
        bindLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));
    }

    /**
     * Registra a persistência permanente de anexo. É usada para consultar
     * anexos salvos anteriormente e no momento de salvar o anexos que estavam
     * na persitência temporária.
     */
    public void setAttachmentPersistencePermanentHandler(ServiceRef<IAttachmentPersistenceHandler> ref) {
        bindLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));

    }

    /**
     * Retorna o serviço responsável por persistir temporáriamente os novos
     * anexos incluidos durante a edição. Se o formulário não for salvo, então
     * os anexos adicionados nesse serviços deverão ser descartados pelo mesmo.
     *
     * @return Nunca null. Retorna {@link InMemoryAttachmentPersitenceHandler}
     *         por default.
     */
    public IAttachmentPersistenceHandler getAttachmentPersistenceTemporaryHandler() {
        IAttachmentPersistenceHandler ref = lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        if (ref == null) {
            ref = new InMemoryAttachmentPersitenceHandler();
            setAttachmentPersistenceTemporaryHandler(ServiceRef.of(ref));
        }
        return ref;
    }

    /**
     * Retorna o serviço responsável por consultar os anexos já salvos
     * anteriormente e responsável por gravar os novos anexos, que estavam na
     * persistencia temporária, no momento de salvar o formulário.
     */
    public IAttachmentPersistenceHandler getAttachmentPersistencePermanentHandler() {
        IAttachmentPersistenceHandler h = lookupLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
        if (h == null) {
            throw new SingularFormException("Não foi configurado o serviço de persitência permanente de anexo. Veja os métodos "
                    + SDocument.class.getName() + ".setAttachmentPersistencePermanentHandler() e " + SDocumentFactory.class.getName());
        }
        return h;
    }

    /**
     * Obtêm a instância que representa o documento com um todo.
     */
    public SInstance getRoot() {
        if (root == null) {
            throw new SingularFormException("Instancia raiz não foi configurada");
        }
        return root;
    }

    public final void setRoot(SInstance root) {
        if (this.root != null) {
            throw new SingularFormException("Não é permitido altera o raiz depois que o mesmo for diferente de null");
        }
        this.root = Objects.requireNonNull(root);
        MTypes.streamDescendants(getRoot().getType(), true).forEach(tipo -> {
            // init dependencies
            final Supplier<Collection<SType<?>>> func = tipo.getValorAtributo(SPackageBasic.ATR_DEPENDS_ON_FUNCTION);
            if (func != null) {
                for (SType<?> dependency : func.get())
                    dependency.getDependentTypes().add(tipo);
            }
        });
    }

    /** USO INTERNO. */
    public SDocumentFactoryRef getDocumentFactoryRef() {
        return documentFactory == null ? null : documentFactory.getDocumentFactoryRef();
    }

    /** USO INTERNO. */
    public final void setDocumentFactory(SDocumentFactory context) {
        if (documentFactory != null) {
            throw new SingularFormException("O contexto do documento não pode ser alteado depois de definido");
        }
        documentFactory = context;
        if (context.getDocumentFactoryRef() == null) {
            throw new SingularFormException(
                    context.getClass().getName() + ".getDocumentContextRef() retorna null. Isso provocará erro de serialização.");
        }
        ServiceRegistry sr = documentFactory.getServiceRegistry();
        if (sr != null) {
            addServiceRegistry(sr);
        }
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

    public void updateAttributes(IMInstanceListener listener) {
        updateAttributes(getRoot(), listener);
    }

    public void updateAttributes(SInstance root, IMInstanceListener listener) {
        if (listener != null)
            getInstanceListeners().add(MInstanceEventType.ATTRIBUTE_CHANGED, listener);

        try {
            MInstances.visitAll(root, true, instance -> {
                instance.updateExists();
                instance.updateObrigatorio();
                MInstances.updateBooleanAttribute(instance, SPackageBasic.ATR_ENABLED, SPackageBasic.ATR_ENABLED_FUNCTION);
                MInstances.updateBooleanAttribute(instance, SPackageBasic.ATR_VISIVEL, SPackageBasic.ATR_VISIBLE_FUNCTION);
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
        IAttachmentPersistenceHandler persistent = getAttachmentPersistencePermanentHandler();
        IAttachmentPersistenceHandler temporary = getAttachmentPersistenceTemporaryHandler();
        new AttachmentPersistenceHelper(temporary, persistent).doPersistence(root);
    }

    public SIAnnotation annotation(Integer id) {  return annotationMap.get(id);  }
    public void annotation(Integer id, SIAnnotation annotation) {   this.annotationMap.put(id, annotation);}
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
        this.temporary = Objects.requireNonNull(temporary);
        this.persistent = Objects.requireNonNull(persistent);
    }

    public void doPersistence(SInstance element) {
        if (element instanceof SIAttachment) {
            handleAttachment((SIAttachment) element);
        } else if (element instanceof ICompositeInstance) {
            visitChildrenIfAny((ICompositeInstance) element);
        }
    }

    private void handleAttachment(SIAttachment attachment) {
        moveFromTemporaryToPersistentIfNeeded(attachment);
    }

    private void moveFromTemporaryToPersistentIfNeeded(SIAttachment attachment) {
        if (!Objects.equals(attachment.getFileId(), attachment.getOriginalFileId())) {
            IAttachmentRef fileRef = temporary.getAttachment(attachment.getFileId());
            if (fileRef != null) {
                IAttachmentRef newRef = persistent.addAttachment(fileRef.getContentAsByteArray());
                deleteOldFiles(attachment, fileRef);
                updateFileId(attachment, newRef);
            }
        }
    }

    private void deleteOldFiles(SIAttachment attachment, IAttachmentRef fileRef) {
        temporary.deleteAttachment(fileRef.getId());
        persistent.deleteAttachment(attachment.getOriginalFileId());
    }

    private void updateFileId(SIAttachment attachment, IAttachmentRef newRef) {
        attachment.setFileId(newRef.getId());
        attachment.setOriginalFileId(newRef.getId());
    }

    private void visitChildrenIfAny(ICompositeInstance composite) {
        if (!composite.getAllChildren().isEmpty()) {
            visitAllChildren(composite);
        }
    }

    private void visitAllChildren(ICompositeInstance composite) {
        for (SInstance child : composite.getAllChildren()) {
            doPersistence(child);
        }
    }
}