package br.net.mirante.singular.flow.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.MoreObjects;

import br.net.mirante.singular.commons.util.log.Loggable;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDataService;
import br.net.mirante.singular.flow.util.props.MetaData;
import br.net.mirante.singular.flow.util.props.MetaDataRef;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;
import br.net.mirante.singular.flow.util.view.Lnk;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessDefinition<I extends ProcessInstance>
        implements Comparable<ProcessDefinition<?>>, Loggable {

    private final Class<I> processInstanceClass;

    private String category;

    private String abbreviation;

    private String name;

    private FlowMap flowMap;

    private Integer entityCod;

    private IProcessCreationPageStrategy creationPage;

    private Class<? extends VariableWrapper> variableWrapperClass;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

    private IProcessDataService<I> processDataService;

    private MetaData metaData;

    private final Map<String, ProcessScheduledJob> scheduledJobsByName = new HashMap<>();

    private transient RefProcessDefinition serializableReference;

    protected ProcessDefinition(Class<I> instanceClass) {
        this(instanceClass, VarService.basic());
    }

    protected ProcessDefinition(Class<I> instanceClass, VarService varService) {
        this.processInstanceClass = instanceClass;
        this.variableService = varService;
    }

    public final Class<I> getProcessInstanceClass() {
        return processInstanceClass;
    }

    /**
     * @deprecated mover para a implementacao do alocpro
     */
    //TODO moverparaalocpro
    @Deprecated
    protected final void setVariableWrapperClass(Class<? extends VariableWrapper> variableWrapperClass) {
        this.variableWrapperClass = variableWrapperClass;
        if (variableWrapperClass != null) {
            if (!VariableEnabled.class.isAssignableFrom(processInstanceClass)) {
                throw new SingularFlowException(
                        "A classe " + processInstanceClass.getName() + " não implementa " + VariableEnabled.class.getName()
                                + " sendo que a definição do processo (" + getClass().getName() + ") trabalha com variáveis.");
            }
            newVariableWrapper(variableWrapperClass).configVariables(getVariables());
        }
    }

    private static <T extends VariableWrapper> T newVariableWrapper(Class<T> variableWrapperClass) {
        try {
            return variableWrapperClass.newInstance();
        } catch (Exception e) {
            throw new SingularFlowException("Erro instanciando " + variableWrapperClass.getName(), e);
        }
    }

    /**
     * <p>
     * Retorna a classe do <i>wrapper</i> de variáveis desta definição de
     * processo.
     * </p>
     *
     * @return a classe do <i>wrapper</i>.
     */
    public final Class<? extends VariableWrapper> getVariableWrapperClass() {
        return variableWrapperClass;
    }

    /**
     * <p>
     * Cria e retorna um novo <i>wrapper</i> de variáveis para o tipo informado.
     * </p>
     *
     * @param <T>
     *            o tipo informado.
     * @param variableWrapperClass
     *            a classe do <i>wrapper</i> a ser criado.
     * @return um novo <i>wrapper</i> para o tipo informado.
     */
    public <T extends VariableWrapper> T newInitialVariables(Class<T> variableWrapperClass) {
        verifyVariableWrapperClass(variableWrapperClass);
        T wrapper = newVariableWrapper(variableWrapperClass);
        wrapper.setVariables(new VarInstanceTableProcess(this));
        return wrapper;
    }

    /**
     * <p>
     * Verifica se a classe do <i>wrapper</i> de variáveis desta definição de
     * processo é igual à informada.
     * </p>
     *
     * @param <T>
     *            o tipo do <i>wrapper</i>.
     * @param expectedVariableWrapperClass
     *            a classe esperada para o <i>wrapper</i>.
     * @throws SingularFlowException
     *             caso as classes não sejam iguais.
     */
    final <T extends VariableWrapper> void verifyVariableWrapperClass(Class<T> expectedVariableWrapperClass) {
        if (expectedVariableWrapperClass != variableWrapperClass) {
            throw new SingularFlowException(getClass().getName()
 + " espera que as variáveis sejam do tipo " + variableWrapperClass);
        }
    }

    protected abstract FlowMap createFlowMap();

    /**
     * <p>
     * Retorna o {@link FlowMap} para esta definição de processo.
     * </p>
     *
     * @return o {@link FlowMap}.
     */
    public final FlowMap getFlowMap() {
        if (flowMap == null) {
            synchronized (this) {
                if (flowMap == null) {
                    FlowMap novo = createFlowMap();

                    if (novo.getProcessDefinition() != this) {
                        throw new SingularFlowException("Mapa com definiçao trocada");
                    }

                    novo.verifyConsistency();
                    MBPMUtil.calculateTaskOrder(novo);
                    flowMap = novo;
                }
            }
        }
        return flowMap;
    }

    /**
     * <p>
     * Retorna o serviço de consulta das instâncias deste tipo de processo.
     * </p>
     *
     * @return o serviço de consulta.
     */
    public IProcessDataService<I> getDataService() {
        if (processDataService == null) {
            processDataService = new ProcessDataServiceImpl<>(this);
        }
        return processDataService;
    }

    protected final VarService getVarService() {
        variableService = variableService.deserialize();
        return variableService;
    }

    public final VarDefinitionMap<?> getVariables() {
        if (variableDefinitions == null) {
            variableDefinitions = getVarService().newVarDefinitionMap();
        }
        return variableDefinitions;
    }

    protected final ProcessScheduledJob addScheduledJob(Supplier<Object> impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    protected final ProcessScheduledJob addScheduledJob(Runnable impl, String name) {
        return addScheduledJob(name).call(impl);
    }

    protected final ProcessScheduledJob addScheduledJob(String name) {
        name = StringUtils.trimToNull(name);

        final ProcessScheduledJob scheduledJob = new ProcessScheduledJob(this, name);

        if (scheduledJobsByName.containsKey(name)) {
            throw new SingularFlowException("A Job with name '" + name + "' is already defined.");
        }
        scheduledJobsByName.put(name, scheduledJob);
        return scheduledJob;
    }

    final Collection<ProcessScheduledJob> getScheduledJobs() {
        return CollectionUtils.unmodifiableCollection(scheduledJobsByName.values());
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef, T defaultValue) {
        return metaData == null ? defaultValue : MoreObjects.firstNonNull(getMetaData().get(propRef), defaultValue);
    }

    public <T> T getMetaDataValue(MetaDataRef<T> propRef) {
        return metaData == null ? null : getMetaData().get(propRef);
    }

    protected <T> ProcessDefinition<I> setMetaDataValue(MetaDataRef<T> propRef, T value) {
        getMetaData().set(propRef, value);
        return this;
    }

    public final IEntityProcessVersion getEntity() {
        synchronized (this) {
            if (entityCod == null) {
                try {
                    IEntityProcessVersion entityProcess = Flow.getMbpmBean().getProcessEntityService().generateEntityFor(this);

                    IEntityProcessVersion oldEntity = entityProcess.getProcessDefinition().getLastVersion();
                    if (Flow.getMbpmBean().getProcessEntityService().isNewVersion(oldEntity, entityProcess)) {

                        entityCod = getPersistenceService().saveOrUpdateProcessDefinition(entityProcess).getCod();
                    } else {
                        entityCod = oldEntity.getCod();
                    }
                } catch (Exception e) {
                    throw new SingularFlowException(createErrorMsg("Erro ao criar entidade para o processo"), e);
                }
            }
        }

        final IEntityProcessVersion def = getPersistenceService().retrieveProcessDefinitionByCod(entityCod);
        if (def == null) {
            entityCod = null;
            throw new SingularFlowException(createErrorMsg("Definicao demanda incosistente com o BD: codigo não encontrado"));
        }

        return def;
    }

    public MTask<?> getTask(ITaskDefinition taskDefinition) {
        return getFlowMap().getTask(taskDefinition);
    }

    @Deprecated
    public final Set<IEntityTaskVersion> getEntityNotJavaTasksVersion() {
        // TODO não faz sentido retornar IEntityTask. Deveria ser
        // IEntityTaskDefinition
        return convertToEntityTaskVersion(getFlowMap().getAllTasks().stream().filter(t -> !t.isJava()));
    }

    @Deprecated
    public final Set<IEntityTaskVersion> getEntityPeopleTasksVersion() {
        // TODO não faz sentido retornar IEntityTask. Deveria ser
        // IEntityTaskDefinition
        return convertToEntityTaskVersion(getFlowMap().getPeopleTasks().stream());
    }

    final IEntityTaskVersion getEntityStartTaskVersion() {
        return getEntityTaskVersion(getFlowMap().getStartTask());
    }

    @Deprecated
    public final IEntityTaskVersion getEntityTaskVersionByAbbreviation(String sigla) {
        return getEntityTaskVersion(getFlowMap().getTaskBybbreviation(sigla));
    }

    @Deprecated
    public final IEntityTaskVersion getEntityTaskVersion(MTask<?> task) {
        // TODO esse metodo deve deixar de ser público
        if (task == null) {
            return null;
        }

        IEntityTaskVersion situacao = getEntity().getTask(task.getAbbreviation());
        if (situacao == null) {
            throw new SingularFlowException(createErrorMsg("Dados inconsistentes com o BD"));
        }
        return situacao;
    }

    public final List<IEntityTaskDefinition> getEntityTaskDefinition(ITaskDefinition... task) {
        return Arrays.stream(task).map(t -> getEntityTaskDefinition(t)).collect(Collectors.toList());
    }

    public final List<IEntityTaskDefinition> getEntityTaskDefinition(Collection<? extends ITaskDefinition> tasks) {
        return tasks.stream().map(t -> getEntityTaskDefinition(t)).collect(Collectors.toList());
    }

    public final IEntityTaskDefinition getEntityTaskDefinition(MTask<?> task) {
        return getEntityTaskDefinitionOrException(task.getAbbreviation());
    }

    public final IEntityTaskDefinition getEntityTaskDefinition(ITaskDefinition task) {
        return getEntityTaskDefinitionOrException(task.getKey());
    }

    public final IEntityTaskDefinition getEntityTaskDefinition(String taskAbbreviation) {
        return (taskAbbreviation == null) ? null : getEntity().getTaskDefinition(taskAbbreviation);
    }

    public final IEntityTaskDefinition getEntityTaskDefinitionOrException(String taskAbbreviation) {
        IEntityTaskDefinition taskDefinition = getEntityTaskDefinition(taskAbbreviation);
        if (taskDefinition == null) {
            throw new SingularFlowException(createErrorMsg("Dados inconsistentes com o BD para a task sigla=" + taskAbbreviation));
        }
        return taskDefinition;
    }

    protected final String createErrorMsg(String msg) {
        return "Processo MBPM '" + getName() + "': " + msg;
    }

    public final String getName() {
        if (name == null) {
            getLogger().warn("!!! process definition name not set, using  class simple name !!!");
            name = this.getClass().getSimpleName();
        }
        return name;
    }

    /**
     * @deprecated o termo sigla deve ser substituido por key
     */
    //TODO renomear
    @Deprecated
    public final String getAbbreviation() {
        if (abbreviation == null) {
            getLogger().warn("!!! process definition abbreviation not set, using  class simple name !!!");
            abbreviation = this.getClass().getSimpleName();
        }
        return abbreviation;
    }

    public final String getCategory() {
        if (category == null) {
            getLogger().warn("!!! process definition category not set, using  class simple name !!!");
            category = this.getClass().getSimpleName();
        }
        return category;
    }

    public final Lnk getCreatePageFor(MUser user) {
        return getCreationPageStrategy().getCreatePageFor(this, user);
    }

    protected final IProcessCreationPageStrategy getCreationPageStrategy() {
        return creationPage;
    }

    protected final void setCreationPageStrategy(IProcessCreationPageStrategy creationPage) {
        this.creationPage = creationPage;
    }

    public boolean isCreatedByUser() {
        return creationPage != null;
    }

    public boolean canBeCreatedBy(MUser user) {
        return isCreatedByUser();
    }

    protected String generateAbbreviation() {
        return getClass().getSimpleName();
    }

    protected final void setName(String category, String name) {
        setName(category, generateAbbreviation(), name);
    }

    private void setName(String category, String abbreviation, String name) {
        this.category = category;
        this.abbreviation = abbreviation;
        this.name = name;
    }

    final MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    final <X extends IEntityTaskVersion> Set<X> convertToEntityTaskVersion(Stream<? extends MTask<?>> stream) {
        return (Set<X>) stream.map(t -> getEntityTaskVersion(t)).collect(Collectors.toSet());
    }

    protected final List<I> convertToProcessInstance(List<? extends IEntityProcessInstance> demandas) {
        return demandas.stream().map(e -> convertToProcessInstance(e)).collect(Collectors.toList());
    }

    protected final I convertToProcessInstance(IEntityProcessInstance dadosInstancia) {
        Objects.requireNonNull(dadosInstancia);
        I novo = newUnbindedInstance();
        novo.setInternalEntity(dadosInstancia);
        return novo;
    }

    /**
     * Retorna um novo e vazio ProcessInstance correspondente a definição de
     * processo atual pronto para ser configurado para um novo fluxo.
     *
     * @return Nunca null
     */
    protected I newInstance() {
        I novo = newUnbindedInstance();
        novo.setInternalEntity(createProcessInstance());
        return novo;
    }

    private I newUnbindedInstance() {
        I novo;
        try {
            novo = getProcessInstanceClass().newInstance();
        } catch (Exception e) {
            throw new SingularFlowException(
                    createErrorMsg("Construtor público ausente: " + getProcessInstanceClass().getSimpleName() + "()"), e);
        }
        novo.setProcessDefinition(this);
        return novo;
    }

    final IEntityProcessInstance createProcessInstance() {
        return getPersistenceService().createProcessInstance(getEntity(), getEntityStartTaskVersion());
    }

    final IPersistenceService<IEntityCategory, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityProcessRole, IEntityRole>) Flow
                .getMbpmBean().getPersistenceService();
    }

    /**
     * Retorna uma referência a definição atual que pode ser serializada e
     * deserializada em implicar na serialização de toda definição do processo.
     */
    protected RefProcessDefinition getSerializableReference() {
        if (serializableReference == null) {
            serializableReference = createStaticReference(getClass());
        }
        return serializableReference;
    }

    private static RefProcessDefinition createStaticReference(final Class<? extends ProcessDefinition> processDefinitionClass) {
        // A criação da classe tem que ficar em um método estático de modo que
        // classe anômina não tenha uma referência implicita a
        // ProcessDefinition, o que atrapalharia a serialização
        return new RefProcessDefinition() {
            @Override
            protected ProcessDefinition<?> reload() {
                return ProcessDefinitionCache.getDefinition(processDefinitionClass);
            }
        };
    }

    @Override
    public int compareTo(ProcessDefinition<?> dp2) {
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

        ProcessDefinition<?> that = (ProcessDefinition<?>) o;

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
