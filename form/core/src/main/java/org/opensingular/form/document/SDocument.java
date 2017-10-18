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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;
import org.opensingular.form.STypes;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.context.DelegatingLocalServiceRegistry;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceListeners;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.annotation.DocumentAnnotations;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.lib.commons.context.RefService;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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

    private boolean restoreMode = false;

    private SInstance root;

    private int lastId = 0;

    private SInstanceListeners instanceListeners;

    private final DelegatingLocalServiceRegistry registry;

    private RefType rootRefType;

    private SDocumentFactory documentFactory;

    private final DocumentAnnotations documentAnnotations = new DocumentAnnotations(this);

    private SetMultimap<Integer, ValidationError> validationErrors;

    public SDocument() {
        registry = new DelegatingLocalServiceRegistry(ServiceRegistryLocator.locate());
    }

    /**
     * Prevents initialization listeners from running
     * and disables internal id increment.
     */
    public void initRestoreMode(){
        this.restoreMode = true;
    }

    /**
     * Signal that the restore mode has ended.
     * Id generation and init listeners are enabled again
     */
    public void finishRestoreMode(){
        this.restoreMode = false;
    }

    /**
     * check if the document is currently under restore mode.
     * @return
     */
    public boolean isRestoreMode(){
        return this.restoreMode;
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

    /**
     * Retorna null se estiver no modo de restore da persistencia.
     */
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
    public void setAttachmentPersistenceTemporaryHandler(RefService<? extends IAttachmentPersistenceHandler<?>> ref) {
        bindLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));
    }

    /**
     * Registra a persistência permanente de anexo. É usada para consultar
     * anexos salvos anteriormente e no momento de salvar o anexos que estavam
     * na persitência temporária.
     */
    public void setAttachmentPersistencePermanentHandler(RefService<? extends IAttachmentPersistenceHandler<?>> ref) {
        bindLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class, Objects.requireNonNull(ref));

    }

    public boolean isAttachmentPersistenceTemporaryHandlerSupported() {
        return lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class).isPresent();
    }

    /**
     * Retorna o serviço responsável por persistir temporáriamente os novos
     * anexos incluidos durante a edição. Se o formulário não for salvo, então
     * os anexos adicionados nesse serviços deverão ser descartados pelo mesmo.
     *
     * @return Nunca null. Retorna {@link InMemoryAttachmentPersistenceHandler}
     * por default.
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public IAttachmentPersistenceHandler<? extends IAttachmentRef> getAttachmentPersistenceTemporaryHandler() {
        Optional<IAttachmentPersistenceHandler> ref = lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        if (ref.isPresent()) {
            return ref.get();
        }
        InMemoryAttachmentPersistenceHandler ref2 = new InMemoryAttachmentPersistenceHandler();
        setAttachmentPersistenceTemporaryHandler(RefService.of(ref2));
        return ref2;
    }

    /**
     * Retorna o serviço responsável por consultar os anexos já salvos
     * anteriormente e responsável por gravar os novos anexos, que estavam na
     * persistencia temporária, no momento de salvar o formulário.
     */
    @SuppressWarnings("unchecked")
    public Optional<IAttachmentPersistenceHandler<? extends IAttachmentRef>> getAttachmentPersistencePermanentHandler() {
        return (Optional<IAttachmentPersistenceHandler<?>>) (Optional) lookupLocalService(FILE_PERSISTENCE_SERVICE,
                IAttachmentPersistenceHandler.class);
    }

    @SuppressWarnings("unchecked")
    private IAttachmentPersistenceHandler<? extends IAttachmentRef>
    getAttachmentPersistencePermanentHandlerOrException() {
        return getLocalRegistry().lookupServiceOrException(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
    }

    /**
     * Obtêm a instância que representa o documento com um todo.
     */
    @Nonnull
    public SInstance getRoot() {
        if (root == null) {
            throw new SingularFormException("Instancia raiz não foi configurada");
        }
        return root;
    }

    public final void setRoot(SInstance root) {
        if (this.root != null) {
            throw new SingularFormException("Não é permitido alterar o raiz depois que o mesmo for diferente de null");
        }
        this.root = Objects.requireNonNull(root);
        STypes.streamDescendants(getRoot().getType(), true).forEach(tipo -> {
            // init dependencies
            final Supplier<Collection<AtrBasic.DelayedDependsOnResolver>> func = tipo.getAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION);
            if (func != null) {
                for (AtrBasic.DelayedDependsOnResolver resolver : func.get()) {
                    for (SType s : resolver.resolve(getRoot().getType(), tipo)){
                        s.addDependentType(tipo);
                    }
                }
            }
        });
    }

    /**
     * USO INTERNO. Retorna a fábrica reponsável pela criação do documento atual
     * (se tiver sido utilizada uma fábrica ao criar o documento).
     */
    public @Nullable RefSDocumentFactory getDocumentFactoryRef() {
        return documentFactory == null ? null : documentFactory.getDocumentFactoryRef();
    }

    final void setDocumentFactory(@Nonnull SDocumentFactory context) {
        if (documentFactory != null) {
            throw new SingularFormException("O contexto do documento não pode ser alteado depois de definido");
        }
        documentFactory = context;
    }


    /**
     * Retorna uma registry local para uso interno do Singular apenas.
     * @return
     */
    @Nonnull
    public ServiceRegistry getLocalRegistry() {
        return registry;
    }

    /**
     * Tenta encontrar um serviço da classe solicitada supondo que o nome no registro é o nome da própria classe. Senão
     * encontrar, então dispara exception.
     */
    @Nonnull
    public <T> T lookupLocalServiceOrException(@Nonnull Class<T> targetClass) {
        return lookupLocalService(targetClass).orElseThrow(() -> new SingularFormException(
                "O serviço " + targetClass.getName() +
                        " não está configurado na instância (no Document). Provavelmente o DocumentFactory não foi " +
                        "configurado corretamente", getRoot()));
    }


    /**
     * Tenta encontrar um serviço da classe solicitada registrado <u>diretamente no documento</u> supondo que o nome no
     * registro é o nome da própria classe.
     */
    public <T> Optional<T> lookupLocalService(Class<T> targetClass) {
        return getLocalRegistry().lookupService(targetClass);
    }

    /**
     * Tenta encontrar um serviço registrado <u>diretamente no documento</u> com
     * o nome informado. Se o resultado não for null e não implementar a classe
     * solicitada, dispara exception.
     */
    @Nonnull
    public <T> Optional<T> lookupLocalService(@Nonnull String name, @Nonnull Class<T> targetClass) {
        return getLocalRegistry().lookupService(name, targetClass);
    }

    /**
     * Registar um serviço com o nome da classe informada. O provider pode ser
     * uma classe derivada da registerClass.
     */
    public <T> void bindLocalService(Class<T> registerClass, RefService<? extends T> provider) {
        getLocalRegistry().bindService(registerClass, provider);
    }

    /**
     * Registar um serviço com o nome informado.
     */
    public <T> void bindLocalService(String serviceName, Class<T> registerClass, RefService<? extends T> provider) {
        getLocalRegistry().bindService(serviceName, registerClass, provider);
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

    public void persistFiles() {
        IAttachmentPersistenceHandler<?> persistent = getAttachmentPersistencePermanentHandlerOrException();
        IAttachmentPersistenceHandler<?> temporary  = getAttachmentPersistenceTemporaryHandler();
        persistent.getAttachmentPersistenceHelper().doPersistence(this, temporary, persistent);
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

    private SDictionary dictionary() {
        return root.getDictionary();
    }

    /** Retorna o serviço de anotação para do documento. */
    public DocumentAnnotations getDocumentAnnotations() {
        return documentAnnotations;
    }

    public Optional<SInstance> findInstanceById(Integer instanceId) {
        //TODO (by Daniel) otimizar esse método. Faz pesquisa em profundidade e poderia ser indexado.
        return SInstances.streamDescendants(getRoot(), true)
                .filter(it -> instanceId.equals(it.getId()))
                .findAny();
    }

    public Collection<ValidationError> getValidationErrors() {
        return validationErrors == null ? Collections.emptyList() : validationErrors.values();
    }

    public Map<Integer, Collection<ValidationError>> getValidationErrorsByInstanceId() {
        if (validationErrors == null) {
            return Collections.emptyMap();
        }
        ArrayListMultimap<Integer, ValidationError> copy = ArrayListMultimap.create();
        copy.putAll(validationErrors());
        return copy.asMap();
    }

    public Set<ValidationError> getValidationErrors(Integer instanceId) {
        return validationErrors == null ? Collections.emptySet() : validationErrors().get(instanceId);
    }

    public void clearValidationErrors(Integer instanceId) {
        if (validationErrors != null) {
            validationErrors().removeAll(instanceId);
        }
    }

    public Set<ValidationError> setValidationErrors(Integer instanceId, Iterable<ValidationError> errors) {
        return validationErrors().replaceValues(instanceId, errors);
    }

    public void setValidationErrors(Iterable<ValidationError> errors) {
        validationErrors = null;
        for (ValidationError error : errors) {
            validationErrors().put(error.getInstanceId(), error);
        }
    }

    private SetMultimap<Integer, ValidationError> validationErrors() {
        if (validationErrors == null) {
            validationErrors = LinkedHashMultimap.create();
        }
        return validationErrors;
    }

}

