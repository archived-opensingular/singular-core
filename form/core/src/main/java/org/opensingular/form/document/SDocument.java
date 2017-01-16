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
import org.opensingular.form.*;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceListeners;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.annotation.DocumentAnnotations;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.type.core.attachment.handlers.InMemoryAttachmentPersistenceHandler;
import org.opensingular.form.validation.IValidationError;

import javax.annotation.Nullable;
import java.util.*;
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

    private SInstance root;

    private int lastId = 0;

    private SInstanceListeners instanceListeners;

    private final DefaultServiceRegistry registry = new DefaultServiceRegistry();

    private RefType rootRefType;

    private SDocumentFactory documentFactory;

    private final DocumentAnnotations documentAnnotations = new DocumentAnnotations(this);

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
     * @return Nunca null. Retorna {@link InMemoryAttachmentPersistenceHandler}
     * por default.
     */
    @SuppressWarnings("unchecked")
    public IAttachmentPersistenceHandler<? extends IAttachmentRef> getAttachmentPersistenceTemporaryHandler() {
        IAttachmentPersistenceHandler<? extends IAttachmentRef> ref = lookupLocalService(FILE_TEMPORARY_SERVICE, IAttachmentPersistenceHandler.class);
        if (ref == null) {
            ref = new InMemoryAttachmentPersistenceHandler();
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
        return lookupLocalService(FILE_PERSISTENCE_SERVICE, IAttachmentPersistenceHandler.class);
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
    public @Nullable RefSDocumentFactory getDocumentFactoryRef() {
        return documentFactory == null ? null : documentFactory.getDocumentFactoryRef();
    }

    /**
     * USO INTERNO.
     */
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
     * to the default one.
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
     * Tenta encontrar um serviço da classe solicitada registrado <u>diretamente no documento</u> supondo que o nome no
     * registro é o nome da própria classe.
     *
     * @return Null se não encontrado ou se o conteúdo do registro for null.
     */
    public <T> T lookupLocalService(Class<T> targetClass) {
        return registry.lookupLocalService(targetClass);
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

    public void persistFiles() {
        IAttachmentPersistenceHandler<? extends IAttachmentRef> persistent = getAttachmentPersistencePermanentHandler();
        IAttachmentPersistenceHandler<? extends IAttachmentRef> temporary  = getAttachmentPersistenceTemporaryHandler();
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

    public Collection<IValidationError> getValidationErrors() {
        return validationErrors == null ? Collections.emptyList() : validationErrors.values();
    }

    public Map<Integer, Collection<IValidationError>> getValidationErrorsByInstanceId() {
        if (validationErrors == null) {
            return Collections.emptyMap();
        }
        ArrayListMultimap<Integer, IValidationError> copy = ArrayListMultimap.create();
        copy.putAll(validationErrors());
        return copy.asMap();
    }

    public Set<IValidationError> getValidationErrors(Integer instanceId) {
        return validationErrors == null ? Collections.emptySet() : validationErrors().get(instanceId);
    }

    public void clearValidationErrors(Integer instanceId) {
        if (validationErrors != null) {
            validationErrors().removeAll(instanceId);
        }
    }

    public Set<IValidationError> setValidationErrors(Integer instanceId, Iterable<IValidationError> errors) {
        return validationErrors().replaceValues(instanceId, errors);
    }

    public void setValidationErrors(Iterable<IValidationError> errors) {
        validationErrors = null;
        for (IValidationError error : errors) {
            validationErrors().put(error.getInstanceId(), error);
        }
    }

    private SetMultimap<Integer, IValidationError> validationErrors() {
        if (validationErrors == null) {
            validationErrors = LinkedHashMultimap.create();
        }
        return validationErrors;
    }

}

