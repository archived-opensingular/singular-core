/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.annotation.AnnotationClassifier;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.RefService;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.annotation.STypeAnnotationList;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.validation.IValidationError;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import org.opensingular.form.SIList;
import org.opensingular.form.STypes;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceListeners;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;

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

    public static final String                     FILE_TEMPORARY_SERVICE   = "fileTemporary";
    public static final String                     FILE_PERSISTENCE_SERVICE = "filePersistence";

    private SInstance root;

    private int lastId = 0;

    private SInstanceListeners instanceListeners;

    private DefaultServiceRegistry registry = new DefaultServiceRegistry();

    private RefType rootRefType;

    private SDocumentFactory documentFactory;

    private SIList<SIAnnotation> annotations;

    private SetMultimap<Integer, IValidationError> validationErrors;

    public SDocument() {
    }

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
    public void setAttachmentPersistenceTemporaryHandler(RefService<IAttachmentPersistenceHandler<? extends IAttachmentRef>> ref) {
        bindLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));
    }

    /**
     * Registra a persistência permanente de anexo. É usada para consultar
     * anexos salvos anteriormente e no momento de salvar o anexos que estavam
     * na persitência temporária.
     */
    public void setAttachmentPersistencePermanentHandler(RefService<IAttachmentPersistenceHandler<? extends IAttachmentRef>> ref) {
        bindLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));

    }

    public boolean isAttachmentPersistenceTemporaryHandlerSupported() {
        return lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class) != null;
    }

    /**
     * Retorna o serviço responsável por persistir temporáriamente os novos
     * anexos incluidos durante a edição. Se o formulário não for salvo, então
     * os anexos adicionados nesse serviços deverão ser descartados pelo mesmo.
     *
     * @return Nunca null. Retorna {@link InMemoryAttachmentPersitenceHandler}
     *         por default.
     */
    @SuppressWarnings("unchecked")
    public IAttachmentPersistenceHandler<? extends IAttachmentRef> getAttachmentPersistenceTemporaryHandler() {
        IAttachmentPersistenceHandler<? extends IAttachmentRef> ref = lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        if (ref == null) {
            ref = new InMemoryAttachmentPersitenceHandler();
            setAttachmentPersistenceTemporaryHandler(RefService.of(ref));
        }
        return ref;
    }

    public boolean isAttachmentPersistencePermanentHandlerSupported() {
        return lookupLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class) != null;
    }

    /**
     * Retorna o serviço responsável por consultar os anexos já salvos
     * anteriormente e responsável por gravar os novos anexos, que estavam na
     * persistencia temporária, no momento de salvar o formulário.
     */
    @SuppressWarnings("unchecked")
    public IAttachmentPersistenceHandler<? extends IAttachmentRef> getAttachmentPersistencePermanentHandler() {
        IAttachmentPersistenceHandler<? extends IAttachmentRef> h = lookupLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
        //        if (h == null) {
        //            throw new SingularFormException("Não foi configurado o serviço de persitência permanente de anexo. Veja os métodos "
        //                + SDocument.class.getName() + ".setAttachmentPersistencePermanentHandler() e " + SDocumentFactory.class.getName());
        //        }
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
        STypes.streamDescendants(getRoot().getType(), true).forEach(tipo -> {
            // init dependencies
            final Supplier<Collection<SType<?>>> func = tipo.getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION);
            if (func != null) {
                for (SType<?> dependency : func.get()) {
                    dependency.addDependentType(tipo);
                }
            }
        });
    }

    /**
     * USO INTERNO. Retorna a fábrica reponsável pela criação do documento atual
     * (se tiver sido utilizada uma fábrica ao criar o documento).
     */
    public RefSDocumentFactory getDocumentFactoryRef() {
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
    public Map<String, ServiceRegistry.Pair> getLocalServices() {
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
    public <T> void bindLocalService(Class<T> registerClass, RefService<? extends T> provider) {
        registry.bindLocalService(registerClass, provider);
    }

    /**
     * Registar um serviço com o nome informado.
     */
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        registry.bindLocalService(serviceName, registerClass, provider);
    }

    public SInstanceListeners getInstanceListeners() {
        if (this.instanceListeners == null)
            this.instanceListeners = new SInstanceListeners();
        return this.instanceListeners;
    }

    public void updateAttributes(ISInstanceListener listener) {
        updateAttributes(getRoot(), listener);
    }

    public void updateAttributes(SInstance root, ISInstanceListener listener) {
        if (listener != null)
            getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, listener);

        try {
            SInstances.visitPostOrder(root, (instance, v) -> {
                instance.updateExists();
                instance.updateRequired();
                SInstances.updateBooleanAttribute(instance, SPackageBasic.ATR_ENABLED, SPackageBasic.ATR_ENABLED_FUNCTION);
                SInstances.updateBooleanAttribute(instance, SPackageBasic.ATR_VISIBLE, SPackageBasic.ATR_VISIBLE_FUNCTION);
            });
        } finally {
            getInstanceListeners().remove(SInstanceEventType.ATTRIBUTE_CHANGED, listener);
        }
    }

    //TODO: Review how this method works. It'd be better if the developer did
    //  not had to remember to call this before saving in the database.
    //  Maybe if the document worked as an Active Record, we'd be able to
    //  intercept the persist call and do this job before the model
    //  would be persisted.
    public void persistFiles() {
        IAttachmentPersistenceHandler<? extends IAttachmentRef> persistent = getAttachmentPersistencePermanentHandler();
        IAttachmentPersistenceHandler<? extends IAttachmentRef> temporary = getAttachmentPersistenceTemporaryHandler();
        new AttachmentPersistenceHelper(temporary, persistent).doPersistence(root);
    }

    /**
     * Referência serializável ao tipo raiz do documento, se o documento tiver
     * sido criada usando uma referência em vez de diretamente com o tipo.
     */
    public final Optional<RefType> getRootRefType() {
        return Optional.ofNullable(rootRefType);
    }

    final void setRootRefType(RefType rootRefType) {
        if (this.rootRefType != null) {
            throw new SingularFormException("Não pode ser trocado");
        }
        this.rootRefType = rootRefType;
    }

    public <T extends Enum<T> & AnnotationClassifier> SIAnnotation annotation(Integer id, T classifier) {
        if (annotations == null)
            return null;
        for (SIAnnotation a : (List<SIAnnotation>) annotations.getValues()) {
            if (id.equals(a.getTargetId()) && classifier.name().equals(a.getClassifier())) {
                return a;
            }
        }
        return null;
    }

    public <T extends Enum<T> & AnnotationClassifier> List<SIAnnotation> annotationsAnyClassifier(Integer id) {
        List<SIAnnotation> siAnnotationList = new ArrayList<SIAnnotation>();
        if (annotations == null)
            return null;
        for (SIAnnotation a : (List<SIAnnotation>) annotations.getValues()) {
            if (id.equals(a.getTargetId())) {
                siAnnotationList.add(a);
            }
        }
        return siAnnotationList;
    }

    private void setAnnotations(SIList<SIAnnotation> annotations) {
        this.annotations = annotations;
    }

    private SDictionary dictionary() {
        return root.getDictionary();
    }

    @SuppressWarnings("unchecked")
    private SIList<SIAnnotation> newAnnotationList() {
        if (getRootRefType().isPresent()) {
            RefType refTypeAnnotation = getRootRefType().get().createSubReference(STypeAnnotationList.class);
            if (getDocumentFactoryRef() != null) {
                return (SIList<SIAnnotation>) getDocumentFactoryRef().get().createInstance(refTypeAnnotation);
            }
            return (SIList<SIAnnotation>) SDocumentFactory.empty().createInstance(refTypeAnnotation);
        }
        return dictionary().newInstance(STypeAnnotationList.class);
    }

    public SIAnnotation newAnnotation() {
        createAnnotationsIfNeeded();
        return (SIAnnotation) annotations.addNew();
    }

    private void createAnnotationsIfNeeded() {
        if (annotations == null)
            setAnnotations(newAnnotationList());
    }

    public SIList<SIAnnotation> annotations() {
        return annotations;
    }

    public Optional<SInstance> findInstanceById(Integer instanceId) {
        return SInstances.findDescendantById(getRoot(), instanceId);
    }
    public Collection<IValidationError> getValidationErrors() {
        return validationErrors().values();
    }
    public Map<Integer, Collection<IValidationError>> getValidationErrorsByInstanceId() {
        ArrayListMultimap<Integer, IValidationError> copy = ArrayListMultimap.create();
        copy.putAll(validationErrors());
        return copy.asMap();
    }
    public Set<IValidationError> getValidationErrors(Integer instanceId) {
        return validationErrors().get(instanceId);
    }
    public Set<IValidationError> clearValidationErrors(Integer instanceId) {
        return setValidationErrors(instanceId, Collections.emptyList());
    }
    public Set<IValidationError> setValidationErrors(Integer instanceId, Iterable<IValidationError> errors) {
        Set<IValidationError> removed = validationErrors().removeAll(instanceId);
        validationErrors().putAll(instanceId, errors);
        return removed;
    }
    public void setValidationErrors(Iterable<IValidationError> errors) {
        validationErrors().clear();
        for (IValidationError error : errors)
            validationErrors().put(error.getInstanceId(), error);
    }
    private SetMultimap<Integer, IValidationError> validationErrors() {
        if (validationErrors == null)
            validationErrors = LinkedHashMultimap.create();
        return validationErrors;
    }

    /**
     * Responsible for moving files from temporary state to persistent.
     *
     * @author Fabricio Buzeto
     *
     */
    private static class AttachmentPersistenceHelper {

        private IAttachmentPersistenceHandler<? extends IAttachmentRef> temporary, persistent;

        public AttachmentPersistenceHelper(
            IAttachmentPersistenceHandler<? extends IAttachmentRef> temporary,
            IAttachmentPersistenceHandler<? extends IAttachmentRef> persistent) {

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
                    IAttachmentRef newRef = persistent.copy(fileRef);
                    deleteOldFiles(attachment, fileRef);
                    updateFileId(attachment, newRef);
                } else if(attachment.getOriginalFileId() != null){
                    persistent.deleteAttachment(attachment.getOriginalFileId());
                }
            }
        }

        private void deleteOldFiles(SIAttachment attachment, IAttachmentRef fileRef) {
            temporary.deleteAttachment(fileRef.getId());
            if(attachment.getOriginalFileId() != null){
                persistent.deleteAttachment(attachment.getOriginalFileId());
            }
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
}
