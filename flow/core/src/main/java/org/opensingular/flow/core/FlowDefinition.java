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
import org.opensingular.flow.core.property.MetaData;
import org.opensingular.flow.core.property.MetaDataEnabled;
import org.opensingular.flow.core.service.IFlowDefinitionEntityService;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IProcessDataService;
import org.opensingular.flow.core.variable.VarDefinitionMap;
import org.opensingular.flow.core.variable.VarService;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.support.spring.injection.SingularSpringInjector;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * Esta é a classe responsável por manter as definições de um dado processo.
 * </p>
 *
 * @param <I>
 *            o tipo das instâncias deste processo.
 * @author Daniel Bordin
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class FlowDefinition<I extends FlowInstance>
        implements Comparable<FlowDefinition<?>>, MetaDataEnabled {

    static final Logger logger = LoggerFactory.getLogger(FlowDefinition.class);

    private final Class<I> flowInstanceClass;

    private final String key;

    private String category;

    private String name;

    private FlowMap flowMap;

    private Integer entityVersionCod;

    private IProcessCreationPageStrategy creationPage;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

    private IProcessDataService<I> processDataService;

    private MetaData metaData;

    private final Map<String, ProcessScheduledJob> scheduledJobsByName = new HashMap<>();

    private transient RefFlowDefinition serializableReference;
    private transient SingularInjector     injector;


    /**
     * <p>
     * Instancia uma nova definição de processo do tipo informado.
     * </p>
     *
     * @param key
     *            a chave do processo.
     * @param instanceClass
     *            o tipo da instância da definição a ser instanciada.
     */
    protected FlowDefinition(Class<I> instanceClass) {
        this(instanceClass, VarService.basic(), null);
    }

    /**
     * <p>
     * Instancia uma nova definição de processo do tipo informado.
     * </p>
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
            throw new SingularFlowException("O nome simples da classe do processo(" + getClass().getSimpleName() +
                    ") não pode ser igual a chave definida em @DefinitionInfo.", this);
        }
        return key;
    }

    /**
     * <p>
     * Retorna o tipo das instâncias desta definição de processo.
     * </p>
     *
     * @return o tipo das instâncias.
     */
    public final Class<I> getFlowInstanceClass() {
        return flowInstanceClass;
    }


    /**
     * Método a ser implementado com a criação do fluxo do processo.
     */
    @Nonnull
    protected abstract FlowMap createFlowMap();

    /**
     * Retorna o {@link FlowMap} para esta definição de processo.
     */
    @Nonnull
    public synchronized final FlowMap getFlowMap() {
        if (flowMap == null) {
            FlowMap newFlow = createFlowMap();
            Objects.requireNonNull(newFlow);
            if (newFlow.getFlowDefinition() != this) {
                throw new SingularFlowException("Mapa com definiçao trocada", this);
            }
            newFlow.verifyConsistency();
            SFlowUtil.calculateTaskOrder(newFlow);
            flowMap = newFlow;
        }
        return flowMap;
    }

    /**
     * Retorna o serviço de consulta das instâncias deste tipo de processo.
     */
    @Nonnull
    public IProcessDataService<I> getDataService() {
        if (processDataService == null) {
            processDataService = new ProcessDataServiceImpl<>(this);
        }
        return processDataService;
    }

    /**
     * <p>
     * Determina o serviço de consulta das instâncias deste tipo de processo.
     * </p>
     * @param processDataService
     */
    protected void setProcessDataService(IProcessDataService<I> processDataService) {
        this.processDataService = processDataService;
    }
    
    /**
     * <p>
     * Retorna o serviço de consulta das definições de variáveis.
     * </p>
     *
     * @return o serviço de consulta.
     */
    protected final VarService getVarService() {
        variableService = variableService.deserialize();
        return variableService;
    }

    /**
     * <p>
     * Retorna as definições de variáveis deste processo.
     * </p>
     *
     * @return the variables
     */
    public final VarDefinitionMap<?> getVariables() {
        if (variableDefinitions == null) {
            variableDefinitions = getVarService().newVarDefinitionMap();
        }
        return variableDefinitions;
    }

    /**
     * <p>
     * Cria e adiciona um novo <i>job</i> ao agendador deste processo.
     * </p>
     *
     * @param impl
     *            a implementação do <i>job</i>.
     * @param name
     *            o nome do <i>job</i>.
     * @return o {@link ProcessScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final ProcessScheduledJob addScheduledJob(Supplier<Object> impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    /**
     * <p>
     * Cria e adiciona um novo <i>job</i> ao agendador deste processo.
     * </p>
     *
     * @param impl
     *            a implementação do <i>job</i>.
     * @param name
     *            o nome do <i>job</i>.
     * @return o {@link ProcessScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final ProcessScheduledJob addScheduledJob(Runnable impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    /**
     * <p>
     * Cria e adiciona um novo <i>job</i> sem implementação ao agendador deste
     * processo.
     * </p>
     *
     * @param name
     *            o nome do <i>job</i>.
     * @return o {@link ProcessScheduledJob} que encapsula o <i>job</i> criado.
     */
    protected final ProcessScheduledJob addScheduledJob(String name) {
        String jobName = StringUtils.trimToNull(name);

        ProcessScheduledJob scheduledJob = new ProcessScheduledJob(this, jobName);

        if (scheduledJobsByName.containsKey(jobName)) {
            throw new SingularFlowException("A Job with name '" + jobName + "' is already defined.", this);
        }
        scheduledJobsByName.put(jobName, scheduledJob);
        return scheduledJob;
    }

    @Nonnull
    final Collection<ProcessScheduledJob> getScheduledJobs() {
        return CollectionUtils.unmodifiableCollection(scheduledJobsByName.values());
    }

    /**
     * <p>
     * Recupera a entidade persistente correspondente a esta definição de
     * processo.
     * </p>
     *
     * @return a entidade persistente.
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
                throw new SingularFlowException(createErrorMsg("Erro ao criar entidade para o processo"), e);
            }
        }

        IEntityFlowVersion version = getPersistenceService().retrieveFlowVersionByCod(entityVersionCod);
        if (version == null) {
            entityVersionCod = null;
            throw new SingularFlowException(
                    createErrorMsg(String.format("Definicao demanda inconsistente com o BD: codigo '%d' não encontrado", entityVersionCod)), this);
        }

        return version;
    }

    public final IEntityFlowDefinition getEntityFlowDefinition() {
        return getEntityFlowVersion().getFlowDefinition();
    }

    /**
     * <p>
     * Encontra a definição da tarefa informada ou dispara uma exceção caso não
     * a encontre.
     * </p>
     *
     * @param taskDefinition
     *            a definição informada.
     * @return a definição da tarefa informada.
     * @throws SingularException
     *             caso não encontre a tarefa.
     */
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
     * <p>
     * Retorna as entidades persistentes correspondentes às definições de
     * tarefas informadas.
     * </p>
     *
     * @param task
     *            as definições informadas.
     * @return as entidades persistentes.
     */
    public final List<IEntityTaskDefinition> getEntityTaskDefinition(ITaskDefinition... task) {
        return Arrays.stream(task).map(this::getEntityTaskDefinition).collect(Collectors.toList());
    }

    /**
     * <p>
     * Retorna as entidades persistentes correspondentes às definições de
     * tarefas informadas.
     * </p>
     *
     * @param tasks
     *            as definições informadas.
     * @return as entidades persistentes.
     */
    public final List<IEntityTaskDefinition> getEntityTaskDefinition(Collection<? extends ITaskDefinition> tasks) {
        return tasks.stream().map(this::getEntityTaskDefinition).collect(Collectors.toList());
    }

    /**
     * <p>
     * Retorna a entidade persistente correspondente à tarefa informada.
     * </p>
     *
     * @param task
     *            a tarefa informada.
     * @return a entidade persistente.
     */
    public final IEntityTaskDefinition getEntityTaskDefinition(STask<?> task) {
        return getEntityTaskDefinitionOrException(task.getAbbreviation());
    }

    /**
     * <p>
     * Retorna a entidade persistente correspondente à definição de tarefa
     * informada.
     * </p>
     *
     * @param task
     *            a definição informada.
     * @return a entidade persistente.
     */
    public final IEntityTaskDefinition getEntityTaskDefinition(ITaskDefinition task) {
        return getEntityTaskDefinitionOrException(task.getKey());
    }

    /**
     * <p>
     * Retorna a entidade persistente correspondente à definição de tarefa com a
     * sigla informada.
     * </p>
     *
     * @param taskAbbreviation
     *            a sigla da definição informada.
     * @return a entidade persistente; ou {@code null} caso não a encontre.
     */
    public final IEntityTaskDefinition getEntityTaskDefinition(String taskAbbreviation) {
        return (taskAbbreviation == null) ? null : getEntityFlowDefinition().getTaskDefinition(taskAbbreviation);
    }

    /**
     * <p>
     * Retorna a entidade persistente correspondente à definição de tarefa com a
     * sigla informada.
     * </p>
     *
     * @param taskAbbreviation
     *            a sigla da definição informada.
     * @return a entidade persistente.
     * @throws SingularFlowException
     *             caso a entidade não seja encontrada.
     */
    public final IEntityTaskDefinition getEntityTaskDefinitionOrException(String taskAbbreviation) {
        IEntityTaskDefinition taskDefinition = getEntityTaskDefinition(taskAbbreviation);
        if (taskDefinition == null) {
            throw new SingularFlowException(
                    createErrorMsg("Dados inconsistentes com o BD para a task sigla=" + taskAbbreviation), this);
        }
        return taskDefinition;
    }

    /**
     * <p>
     * Formata uma mensagem de erro.
     * </p>
     * <p>
     * <p>
     * A formatação da mensagem segue o seguinte padrão:
     * </p>
     * <p>
     *
     * <pre>
     * "Processo MBPM '" + getName() + "': " + msg
     * </pre>
     *
     * @param msg
     *            a mensagem a ser formatada.
     * @return a mensagem formatada.
     * @see #getName()
     */
    protected final String createErrorMsg(String msg) {
        return "Processo '" + getName() + "': " + msg;
    }

    /**
     * <p>Retorna o nome deste processo.</p>
     */
    public final String getName() {
        if (name == null) {
            logger.warn("!!! process definition name not set, using  class simple name !!!");
            name = this.getClass().getSimpleName();
        }
        return name;
    }

    /**
     * <p>Retorna a chave deste processo.</p>
     */
    public final String getKey() {
        return key;
    }

    /**
     * <p>Retorna a categoria deste processo.</p>
     *
     * @return a categoria deste processo.
     */
    public final String getCategory() {
        if (category == null) {
            logger.warn("!!! process definition category not set, using  class simple name !!!");
            category = this.getClass().getSimpleName();
        }
        return category;
    }

    /**
     * <p>
     * Retorna o <i>link resolver</i> deste processo para o usuário
     * especificado.
     * </p>
     *
     * @param user
     *            o usuário especificado.
     * @return o <i>link resolver</i>.
     */
    public final Lnk getCreatePageFor(SUser user) {
        return getCreationPageStrategy().getCreatePageFor(this, user);
    }

    /**
     * <p>Retorna o {@link IProcessCreationPageStrategy} deste processo.</p>
     *
     * @return o {@link IProcessCreationPageStrategy}.
     */
    protected final IProcessCreationPageStrategy getCreationPageStrategy() {
        return creationPage;
    }

    /**
     * <p>Configura o {@link IProcessCreationPageStrategy} deste processo.</p>
     *
     * @param creationPage o {@link IProcessCreationPageStrategy}.
     */
    protected final void setCreationPageStrategy(@Nonnull IProcessCreationPageStrategy creationPage) {
        this.creationPage = inject(creationPage);
    }

    /**
     * <p>
     * Verifica se há um {@link IProcessCreationPageStrategy} configurado.
     * </p>
     *
     * @return {@code true} caso exista um {@link IProcessCreationPageStrategy}
     *         configurado; {@code false} caso contrário.
     */
    public boolean isCreatedByUser() {
        return creationPage != null;
    }

    /**
     * <p>
     * Verifica se um {@link IProcessCreationPageStrategy} possa ser configurado
     * pelo usuário especificado.
     * </p>
     *
     * @param user
     *            o usuário especificado.
     * @return {@code true} caso um {@link IProcessCreationPageStrategy} possa
     *         ser configurado; {@code false} caso contrário.
     */
    public boolean canBeCreatedBy(SUser user) {
        return isCreatedByUser();
    }

    /**
     * <p>
     * Gera uma sigla para esta definição de processo.
     * </p>
     *
     * @return a sigla gerada.
     */
    protected String generateAbbreviation() {
        return getClass().getSimpleName();
    }

    /**
     * <p>
     * Configura a categoria e nome desta definição de processo.
     * </p>
     *
     * @param category
     *            a categoria.
     * @param name
     *            o nome.
     */
    protected final void setName(String category, String name) {
        this.category = category;
        this.name = name;
    }

    @Override
    @Nonnull
    public Optional<MetaData> getMetaDataOpt() {
        return Optional.ofNullable(metaData);
    }

    @Override
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    final <X extends IEntityTaskVersion> Set<X> convertToEntityTaskVersion(Stream<? extends STask<?>> stream) {
        return (Set<X>) stream.map(t -> getEntityTaskVersion(t)).collect(Collectors.toSet());
    }

    /**
     * <p>
     * Retorna uma lista de instâncias correspondentes às entidades fornecidas.
     * </p>
     *
     * @param entities
     *            as entidades fornecidas.
     * @return a lista de instâncias.
     */
    protected final List<I> convertToFlowInstance(List<? extends IEntityFlowInstance> entities) {
        return entities.stream().map(e -> convertToFlowInstance(e)).collect(Collectors.toList());
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

    /** Verifica se a entidade da instancia de processo pertence a definição de processo. Senão dispara Exception. */
    private static void checkIfKeysAreCompatible(FlowDefinition<?> definition, IEntityFlowInstance instance) {
        if(! instance.getFlowVersion().getFlowDefinition().getKey().equalsIgnoreCase(definition.getKey())){
            throw new SingularFlowException(
                    "A instancia de processo com id " + instance.getCod() + " não pertence a definição de processo " +
                            definition.getName(), definition);
        }
    }

    /** Verifica se a instancia de processo pertence a definição de processo. Senão dispara Exception. */
    final void checkIfCompatible(FlowInstance instance) {
        checkIfKeysAreCompatible(this, instance.getEntity());
        if (!flowInstanceClass.isInstance(instance)) {
            throw new SingularFlowException(
                    "A instancia de processo com id=" + instance.getFullId() + " deveria ser da classe " +
                            flowInstanceClass.getName() + " mas na verdade é da classe " +
                            instance.getClass().getName(), instance);
        }
    }

    /**
     * Retorna uma nova instância vazia deste processo pronta para ser
     * configurada em um novo fluxo.
     */
    @Nonnull
    public I newPreStartInstance() {
        I newInstance = newUnbindedInstance();
        newInstance.setInternalEntity(createFlowInstance());
        return newInstance;
    }

    public StartCall<I> prepareStartCall() {
        return new StartCall<I>(this, new RefStart(getFlowMap().getStart()));
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
     * deserializada sem implicar na serialização de toda definição do processo.
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
            injector = result.orElseGet(() -> SingularSpringInjector.get());
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
