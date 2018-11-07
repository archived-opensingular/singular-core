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

package org.opensingular.flow.core;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.property.MetaDataEnabledImpl;
import org.opensingular.flow.core.service.IFlowDataService;
import org.opensingular.flow.core.service.IFlowDefinitionEntityService;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.variable.VarDefinitionMap;
import org.opensingular.flow.core.variable.VarService;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.support.spring.injection.SingularSpringInjector;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.net.Lnk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Esta é a classe responsável por manter as definições de um dado fluxo.
 *
 * @param <I> o tipo das instâncias deste fluxo.
 * @author Daniel Bordin
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class FlowDefinition<I extends FlowInstance> extends MetaDataEnabledImpl
        implements Comparable<FlowDefinition<?>> {

    private static final Logger logger = LoggerFactory.getLogger(FlowDefinition.class);

    private final Class<I> flowInstanceClass;

    private final String key;

    private String category;

    private String name;

    private FlowMap flowMap;

    private Integer entityVersionCod;

    private IFlowCreationPageStrategy creationPage;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

    private IFlowDataService<I> flowDataService;

    private final Map<String, FlowScheduledJob> scheduledJobsByName = new HashMap<>();

    private transient RefFlowDefinition serializableReference;

    private transient SingularInjector     injector;

    private boolean checkForConsistency = true;

    /**
     * Instancia uma nova definição de fluxo do tipo informado.
     *
     * @param instanceClass
     *            a chave do fluxo.
     * @param instanceClass
     *            o tipo da instância da definição a ser instanciada.
     * @param instanceClass o tipo da instância da definição a ser instanciada.
     */
    protected FlowDefinition(Class<I> instanceClass) {
        this(instanceClass, VarService.basic(), null);
    }

    /**
     * Instancia uma nova definição de fluxo do tipo informado.
     *
     * @param flowInstanceClass
     *            o tipo da instância da definição a ser instanciada.
     * @param varService
     *            o serviço de consulta das definições de variáveis.
     */
    protected FlowDefinition(Class<I> flowInstanceClass, VarService varService, @Nullable String flowKey) {
        this.key = resolveFlowKey(flowKey);
        this.flowInstanceClass = Objects.requireNonNull(flowInstanceClass, "flowInstanceClass");
        this.variableService = varService;
        inject(this);
    }

    @Nonnull
    private String resolveFlowKey(@Nullable String flowKey) {
        String key = flowKey;
        if (key == null) {
            if (this.getClass().isAnnotationPresent(DefinitionInfo.class)) {
                key = this.getClass().getAnnotation(DefinitionInfo.class).value();
            }
        }
        key = StringUtils.trimToNull(key);
        if (key == null) {
            throw new SingularFlowException(
                    "A definição de fluxo (classe " + getClass().getName() + ") deve ser anotada com " +
                            DefinitionInfo.class.getName(), this);
        } else if (getClass().getSimpleName().equalsIgnoreCase(key)) {
            throw new SingularFlowException("O nome simples da classe do fluxo(" + getClass().getSimpleName() +
                    ") não pode ser igual a chave definida em @DefinitionInfo.", this);
        }
        return key;
    }

    /**
     * Retorna o tipo das instâncias desta definição de fluxo.
     */
    public final Class<I> getFlowInstanceClass() {
        return flowInstanceClass;
    }


    /**
     * Método a ser implementado com a criação da estrutura do fluxo.
     */
    @Nonnull
    protected abstract FlowMap createFlowMap();

    /**
     * Retorna o {@link FlowMap} para esta definição de fluxo.
     */
    @Nonnull
    public synchronized final FlowMap getFlowMap() {
        if (flowMap == null) {
            FlowMap newFlow = createFlowMap();
            Objects.requireNonNull(newFlow);
            if (newFlow.getFlowDefinition() != this) {
                throw new SingularFlowException("Mapa com definiçao trocada", this);
            }
            if (checkForConsistency) {
                newFlow.verifyConsistency();
            }
            SFlowUtil.calculateTaskOrder(newFlow);
            flowMap = newFlow;
        }
        return flowMap;
    }

    /**
     * Turns off the verification of consistency of the {@link FlowMap}. Should only be used in flows that are not
     * supposed to be started.
     */
    public final synchronized void setCheckForConsistency(boolean value) {
        checkForConsistency = value;
    }

    /**
     * Retorna o serviço de consulta das instâncias deste tipo de fluxo.
     */
    @Nonnull
    public IFlowDataService<I> getDataService() {
        if (flowDataService == null) {
            flowDataService = new FlowDataServiceImpl<>(this);
        }
        return flowDataService;
    }

    /**
     * Determina o serviço de consulta das instâncias deste tipo de fluxo.
     */
    protected void setFlowDataService(IFlowDataService<I> flowDataService) {
        this.flowDataService = flowDataService;
    }
    
    /**
     * Retorna o serviço de consulta das definições de variáveis.
     */
    protected final VarService getVarService() {
        variableService = variableService.deserialize();
        return variableService;
    }

    /**
     * Retorna as definições de variáveis deste fluxo.
     */
    public final VarDefinitionMap<?> getVariables() {
        if (variableDefinitions == null) {
            variableDefinitions = getVarService().newVarDefinitionMap();
        }
        return variableDefinitions;
    }

    /**
     * Cria e adiciona um novo <i>job</i> ao agendador deste fluxo.
     *
     * @param impl a implementação do <i>job</i>.
     * @param name o nome do <i>job</i>.
     * @return o {@link FlowScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final FlowScheduledJob addScheduledJob(ISupplier<Object> impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    /**
     * Cria e adiciona um novo <i>job</i> ao agendador deste fluxo.
     *
     * @param impl a implementação do <i>job</i>.
     * @param name o nome do <i>job</i>.
     * @return o {@link FlowScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final FlowScheduledJob addScheduledJob(Runnable impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    /**
     * Cria e adiciona um novo <i>job</i> sem implementação ao agendador deste
     * fluxo.
     *
     * @param name o nome do <i>job</i>.
     * @return o {@link FlowScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final FlowScheduledJob addScheduledJob(String name) {
        String jobName = StringUtils.trimToNull(name);

        FlowScheduledJob scheduledJob = new FlowScheduledJob(this, jobName);

        if (scheduledJobsByName.containsKey(jobName)) {
            throw new SingularFlowException("A Job with name '" + jobName + "' is already defined.", this);
        }
        scheduledJobsByName.put(jobName, scheduledJob);
        return scheduledJob;
    }

    @Nonnull
    final Collection<FlowScheduledJob> getScheduledJobs() {
        return CollectionUtils.unmodifiableCollection(scheduledJobsByName.values());
    }

    /**
     * Recupera a entidade persistente correspondente a esta definição de
     * fluxo.
     */
    public synchronized final IEntityFlowVersion getEntityFlowVersion() {
        if (entityVersionCod == null) {
            try {
                IFlowDefinitionEntityService<?, ?, ?, ?, ?, ?, ?, ?> flowEntityService = Flow.getConfigBean().getFlowEntityService();
                IEntityFlowVersion newVersion = flowEntityService.generateEntityFor(this);

                IEntityFlowVersion oldVersion = newVersion.getFlowDefinition().getLastVersion();
                if (flowEntityService.isDifferentVersion(oldVersion, newVersion)) {

                    entityVersionCod = getPersistenceService().saveFlowVersion(newVersion).getCod();
                } else {
                    entityVersionCod = oldVersion.getCod();
                }
            } catch (Exception e) {
                throw new SingularFlowException(createErrorMsg("Erro ao criar entidade para o fluxo"), e);
            }
        }

        IEntityFlowVersion version = getPersistenceService().retrieveFlowVersionByCod(entityVersionCod);
        if (version == null) {
            String errorMsg = createErrorMsg(
                    String.format("Definicao demanda inconsistente com o BD: entityVersionCod '%d' não encontrado",
                            entityVersionCod));
            entityVersionCod = null;
            throw new SingularFlowException(errorMsg, this);
        }

        return version;
    }

    public final IEntityFlowDefinition getEntityFlowDefinition() {
        return getEntityFlowVersion().getFlowDefinition();
    }

    /**
     * Encontra a definição da tarefa informada ou dispara uma exceção caso não
     * a encontre.
     *
     * @param taskDefinition a definição informada.
     * @throws SingularFlowException caso não encontre a tarefa.
     */
    @Nonnull
    public STask<?> getTask(ITaskDefinition taskDefinition) {
        return getFlowMap().getTask(taskDefinition);
    }

    public final Set<IEntityTaskDefinition> getEntityTasksDefinitionNotJava() {
        return getEntityFlowDefinition().getTaskDefinitions().stream().filter(t -> !t.getLastVersion().isJava()).collect(Collectors.toSet());
    }

    final @Nonnull IEntityTaskVersion getEntityTaskVersion(@Nonnull STask<?> task) {
        Objects.requireNonNull(task);
        IEntityTaskVersion version = getEntityFlowVersion().getTaskVersion(task.getAbbreviation());
        if (version == null) {
            throw new SingularFlowException(createErrorMsg("Dados inconsistentes com o BD"), this);
        }
        return version;
    }

    /**
     * Retorna as entidades persistentes correspondentes às definições de
     * tarefas informadas.
     *
     * @param task as definições informadas.
     */
    public final List<IEntityTaskDefinition> getEntityTaskDefinition(ITaskDefinition... task) {
        return Arrays.stream(task).map(this::getEntityTaskDefinition).collect(Collectors.toList());
    }

    /**
     * Retorna as entidades persistentes correspondentes às definições de
     * tarefas informadas.
     *
     * @param tasks as definições informadas.
     */
    public final List<IEntityTaskDefinition> getEntityTaskDefinition(Collection<? extends ITaskDefinition> tasks) {
        return tasks.stream().map(this::getEntityTaskDefinition).collect(Collectors.toList());
    }

    /**
     * Retorna a entidade persistente correspondente à tarefa informada.
     *
     * @param task a tarefa informada.
     */
    public final IEntityTaskDefinition getEntityTaskDefinition(STask<?> task) {
        return getEntityTaskDefinitionOrException(task.getAbbreviation());
    }

    /**
     * Retorna a entidade persistente correspondente à definição de tarefa
     * informada.
     *
     * @param task a definição informada.
     */
    public final IEntityTaskDefinition getEntityTaskDefinition(ITaskDefinition task) {
        return getEntityTaskDefinitionOrException(task.getKey());
    }

    /**
     * Retorna a entidade persistente correspondente à definição de tarefa com a
     * sigla informada.
     *
     * @param taskAbbreviation a sigla da definição informada.
     * @return a entidade persistente; ou {@code null} caso não a encontre.
     */
    @Nullable
    public final IEntityTaskDefinition getEntityTaskDefinition(@Nullable String taskAbbreviation) {
        return (taskAbbreviation == null) ? null : getEntityFlowDefinition().getTaskDefinition(taskAbbreviation);
    }

    /**
     * Retorna a entidade persistente correspondente à definição de tarefa com a
     * sigla informada.
     *
     * @param taskAbbreviation a sigla da definição informada.
     * @return a entidade persistente.
     * @throws SingularFlowException caso a entidade não seja encontrada.
     */
    @Nonnull
    public final IEntityTaskDefinition getEntityTaskDefinitionOrException(String taskAbbreviation) {
        IEntityTaskDefinition taskDefinition = getEntityTaskDefinition(taskAbbreviation);
        if (taskDefinition == null) {
            throw new SingularFlowException(
                    createErrorMsg("Dados inconsistentes com o BD para a task sigla=" + taskAbbreviation), this);
        }
        return taskDefinition;
    }

    /**
     * Formata uma mensagem de erro.
     * <p>
     * A formatação da mensagem segue o seguinte padrão:
     * </p>
     *
     * <pre>
     * "Fluxo '" + getName() + "': " + msg
     * </pre>
     *
     * @param msg a mensagem a ser formatada.
     * @see #getName()
     */
    protected final String createErrorMsg(String msg) {
        return "Fluxo '" + getName() + "': " + msg;
    }

    /**
     * Retorna o nome deste fluxo.
     */
    public final String getName() {
        if (name == null) {
            logger.warn("!!! flow definition name not set, using  class simple name !!!");
            name = this.getClass().getSimpleName();
        }
        return name;
    }

    /**
     * Retorna a chave deste fluxo.
     */
    public final String getKey() {
        return key;
    }

    /**
     * Retorna a categoria deste fluxo.
     */
    @Nonnull
    public final String getCategory() {
        if (category == null) {
            logger.warn("!!! flow definition category not set, using  class simple name !!!");
            category = this.getClass().getSimpleName();
        }
        return category;
    }

    /**
     * Retorna o <i>link resolver</i> deste fluxo para o usuário especificado.
     */
    public final Lnk getCreatePageFor(SUser user) {
        return getCreationPageStrategy().getCreatePageFor(this, user);
    }

    /**
     * Retorna o {@link IFlowCreationPageStrategy} deste fluxo.
     */
    protected final IFlowCreationPageStrategy getCreationPageStrategy() {
        return creationPage;
    }

    /**
     * Configura o {@link IFlowCreationPageStrategy} deste fluxo.
     */
    protected final void setCreationPageStrategy(@Nonnull IFlowCreationPageStrategy creationPage) {
        this.creationPage = inject(creationPage);
    }

    /**
     * Verifica se há um {@link IFlowCreationPageStrategy} configurado.
     *
     * @return {@code true} caso exista um {@link IFlowCreationPageStrategy}
     *         configurado; {@code false} caso contrário.
     */
    public boolean isCreatedByUser() {
        return creationPage != null;
    }

    /**
     * Verifica se um {@link IFlowCreationPageStrategy} possa ser configurado
     * pelo usuário especificado.
     *
     * @param user
     *            o usuário especificado.
     * @return {@code true} caso um {@link IFlowCreationPageStrategy} possa
     *         ser configurado; {@code false} caso contrário.
     */
    public boolean canBeCreatedBy(SUser user) {
        return isCreatedByUser();
    }

    /**
     * Gera uma sigla para esta definição de fluxo.
     */
    @Nonnull
    protected String generateAbbreviation() {
        return getClass().getSimpleName();
    }

    /**
     * Configura a categoria e nome desta definição de fluxo.
     */
    protected final void setName(String category, String name) {
        this.category = category;
        this.name = name;
    }

    final <X extends IEntityTaskVersion> Set<X> convertToEntityTaskVersion(Stream<? extends STask<?>> stream) {
        return (Set<X>) stream.map(this::getEntityTaskVersion).collect(Collectors.toSet());
    }

    /**
     * Retorna uma lista de instâncias correspondentes às entidades fornecidas.
     */
    protected final List<I> convertToFlowInstance(List<? extends IEntityFlowInstance> entities) {
        return entities.stream().map(this::convertToFlowInstance).collect(Collectors.toList());
    }

    /**
     * Retorna a instância correspondente à entidade fornecida.
     */
    @Nonnull
    protected final I convertToFlowInstance(@Nonnull IEntityFlowInstance entityFlowInstance) {
        Objects.requireNonNull(entityFlowInstance);
        checkIfKeysAreCompatible(this, entityFlowInstance);

        I newInstance = newUnbindedInstance();
        newInstance.setInternalEntity(entityFlowInstance);
        return newInstance;
    }

    /** Verifica se a entidade da instancia de fluxo pertence a definição de fluxo. Senão dispara Exception. */
    private static void checkIfKeysAreCompatible(FlowDefinition<?> definition, IEntityFlowInstance instance) {
        if(! instance.getFlowVersion().getFlowDefinition().getKey().equalsIgnoreCase(definition.getKey())){
            throw new SingularFlowException(
                    "A instancia de fluxo com id " + instance.getCod() + " não pertence a definição de fluxo " +
                            definition.getName(), definition);
        }
    }

    /** Verifica se a instancia de fluxo pertence a definição de fluxo. Senão dispara Exception. */
    final void checkIfCompatible(FlowInstance instance) {
        checkIfKeysAreCompatible(this, instance.getEntity());
        if (!flowInstanceClass.isInstance(instance)) {
            throw new SingularFlowException(
                    "A instancia de fluxo com id=" + instance.getFullId() + " deveria ser da classe " +
                            flowInstanceClass.getName() + " mas na verdade é da classe " +
                            instance.getClass().getName(), instance);
        }
    }

    /**
     * Retorna uma nova instância vazia deste fluxo pronta para ser
     * configurada em um novo fluxo.
     */
    @Nonnull
    public I newPreStartInstance() {
        I newInstance = newUnbindedInstance();
        newInstance.setInternalEntity(createFlowInstance());
        return newInstance;
    }

    public StartCall<I> prepareStartCall() {
        return new StartCall<>(this, new RefStart(getFlowMap().getStart()));
    }


    @Nonnull
    private I newUnbindedInstance() {
        I newInstance;
        try {
            for (Constructor<?> c : getFlowInstanceClass().getDeclaredConstructors()) {
                if (c.getParameters().length == 0) {
                    c.setAccessible(true);
                    newInstance = (I) c.newInstance();
                    newInstance.setFlowDefinition(this);
                    return newInstance;
                }
            }
        } catch (Exception e) {
            throw new SingularFlowException(e.getMessage(), e);
        }
        throw new SingularFlowException(createErrorMsg(
                "Construtor sem parametros ausente: " + getFlowInstanceClass().getSimpleName() + "()"), this);
    }

    final IEntityFlowInstance createFlowInstance() {
        IEntityTaskVersion initialState =  getEntityTaskVersion(getFlowMap().getStart().getTask());
        return getPersistenceService().createFlowInstance(getEntityFlowVersion(), initialState);
    }

    final IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance>) Flow
                .getConfigBean().getPersistenceService();
    }

    /**
     * Retorna uma referência a definição atual que pode ser serializada e
     * deserializada sem implicar na serialização de toda definição do fluxo.
     *
     * @return referência serializável.
     */
    protected RefFlowDefinition getSerializableReference() {
        if (serializableReference == null) {
            serializableReference = createStaticReference(getClass());
        }
        return serializableReference;
    }

    @Nonnull
    final <V> V inject(@Nonnull V target) {
        if (injector == null) {
            Optional<SingularInjector> result = ServiceRegistryLocator.locate().lookupSingularInjectorOpt();
            injector = result.orElseGet(SingularSpringInjector::get);
        }
        injector.inject(Objects.requireNonNull(target));
        return target;
    }

    private static RefFlowDefinition createStaticReference(final Class<? extends FlowDefinition> flowDefinitionClass) {
        // A criação da classe tem que ficar em um método estático de modo que
        // classe anômina não tenha uma referência implicita a
        // FlowDefinition, o que atrapalharia a serialização
        return new RefFlowDefinition() {
            @Override
            protected FlowDefinition<?> reload() {
                return FlowDefinitionCache.getDefinition(flowDefinitionClass);
            }
        };
    }

    @Override
    public int compareTo(FlowDefinition<?> dp2) {
        int v = getCategory().compareTo(dp2.getCategory());
        if (v == 0) {
            v = getName().compareTo(dp2.getName());
        }
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlowDefinition<?> that = (FlowDefinition<?>) o;

        return category.equals(that.category) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result;
        result = getCategory().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
