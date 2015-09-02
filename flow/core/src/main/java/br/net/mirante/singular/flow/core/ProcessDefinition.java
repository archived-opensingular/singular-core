package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.lang.reflect.Constructor;
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

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.renderer.FlowRendererFactory;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDataService;
import br.net.mirante.singular.flow.util.props.MetaData;
import br.net.mirante.singular.flow.util.props.MetaDataRef;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;

@SuppressWarnings({ "serial", "unchecked" })
public abstract class ProcessDefinition<I extends ProcessInstance> implements Comparable<ProcessDefinition<?>> {

    private final Class<I> instanceClass;

    private String category;

    private String abbreviation;

    private String name;

    private FlowMap flowMap;

    private Serializable entityCod;

    private IProcessCreationPageStrategy creationPage;

    private Class<? extends VariableWrapper> variableWrapperClass;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

    private IProcessDataService<I> processDataService;

    private MetaData metaData;

    private final Map<String, ProcessScheduledJob> scheduledJobsByName = new HashMap<>();

    private transient Constructor<I> construtor;

    protected ProcessDefinition(Class<I> instanceClass) {
        this(instanceClass, VarService.basic());
    }

    protected ProcessDefinition(Class<I> instanceClass, VarService varService) {
        this.instanceClass = instanceClass;
        this.variableService = varService;
        Objects.requireNonNull(getConstrutor());
    }

    public final Class<I> getInstanceClass() {
        return instanceClass;
    }

    protected final void setVariableWrapperClass(Class<? extends VariableWrapper> variableWrapperClass) {
        this.variableWrapperClass = variableWrapperClass;
        if (variableWrapperClass != null) {
            if (!VariableEnabled.class.isAssignableFrom(instanceClass)) {
                throw new SingularFlowException("A classe " + instanceClass.getName() + " não implementa " + VariableEnabled.class.getName()
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

    public final Class<? extends VariableWrapper> getVariableWrapperClass() {
        return variableWrapperClass;
    }

    public <T extends VariableWrapper> T newInitialVariables(Class<T> variableWrapperClass) {
        verifyVariableWrapperClass(variableWrapperClass);
        T wrapper = newVariableWrapper(variableWrapperClass);
        wrapper.setVariables(new VarInstanceTableProcess(this));
        return wrapper;
    }

    final <T extends VariableWrapper> void verifyVariableWrapperClass(Class<T> expectedVariableWrapperClass) {
        if (expectedVariableWrapperClass != variableWrapperClass) {
            throw new SingularFlowException(getClass().getName() + " espera que as variáveis sejam do tipo " + variableWrapperClass);
        }
    }

    protected abstract FlowMap createFlowMap();

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

    public final IEntityProcess getEntity() {
        if (entityCod == null) {
            synchronized (this) {
                if (entityCod == null) {
                    IEntityProcess entityProcess = MBPM.getMbpmBean().getProcessEntityService().generateEntityFor(this);

                    entityCod = entityProcess.getCod();

                    return entityProcess;
                }
            }
        }
        final IEntityProcess def = getPersistenceService().retrieveProcessDefinitionByCod(entityCod);

        if (def == null) {
            entityCod = null;
            throw new SingularFlowException(createErrorMsg("Definicao demanda incosistente com o BD: codigo não encontrado"));
        }

        return def;
    }

    public final Set<IEntityTask> getEntityNotJavaTask() {
        return convertToEntityTask(getFlowMap().getAllTasks().stream().filter(t -> !t.isJava()));
    }

    public final Set<IEntityTask> getEntityPeopleTasks() {
        return convertToEntityTask(getFlowMap().getPeopleTasks().stream());
    }

    public final IEntityTask getEntityStartTask() {
        final MTask<?> inicial = getFlowMap().getStartTask();
        return getEntityTask(inicial);
    }

    public final IEntityTask getEntityTaskWithName(String taskName) {
        return getEntityTask(getFlowMap().getTaskWithName(taskName));
    }

    public final IEntityTask getEntityTaskWithAbbreviation(String sigla) {
        return getEntityTask(getFlowMap().getTaskWithAbbreviation(sigla));
    }

    public final IEntityTask getEntityTask(MTask<?> task) {
        if (task == null) {
            return null;
        }

        IEntityTask situacao = getEntity().getTask(task.getAbbreviation());
        if (situacao == null) {
            throw new SingularFlowException(createErrorMsg("Dados inconsistentes com o BD"));
        }
        return situacao;
    }

    public byte[] getFlowImage() {
        return FlowRendererFactory.generateImageFor(this);
    }

    protected final String createErrorMsg(String msg) {
        return "Processo MBPM '" + getName() + "': " + msg;
    }

    public final String getName() {
        return name;
    }

    public final String getAbbreviation() {
        return abbreviation;
    }

    public final String getCategory() {
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

    final Set<IEntityTask> convertToEntityTask(Collection<? extends MTask<?>> collection) {
        return convertToEntityTask(collection.stream());
    }

    final <X extends IEntityTask> Set<X> convertToEntityTask(Stream<? extends MTask<?>> stream) {
        return (Set<X>) stream.map(this::getEntityTask).collect(Collectors.toSet());
    }

    protected final List<I> convertToProcessInstance(List<? extends IEntityProcessInstance> demandas) {
        return demandas.stream().map(this::convertToProcessInstance).collect(Collectors.toList());
    }

    protected final I convertToProcessInstance(IEntityProcessInstance dadosInstancia) {
        Objects.requireNonNull(dadosInstancia);
        try {
            return getConstrutor().newInstance(dadosInstancia);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private Constructor<I> getConstrutor() {
        if (construtor == null) {
            try {
                for (Constructor<?> constructor : getInstanceClass().getConstructors()) {
                    if (constructor.getParameterTypes().length == 1
                        && IEntityProcessInstance.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                        this.construtor = (Constructor<I>) constructor;
                    }
                }
                Objects.requireNonNull(this.construtor);
            } catch (final Exception e) {
                throw new SingularFlowException(createErrorMsg("Construtor ausente: " + getInstanceClass().getName() + "(" + IEntityProcessInstance.class.getName() + ")"), e);
            }
        }
        return construtor;
    }

    final IEntityProcessInstance createProcessInstance() {
        return getPersistenceService().createProcessInstance(getEntity(), getEntityStartTask());
    }

    final IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTask, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTask, IEntityVariableInstance, IEntityProcessRole, IEntityRole>) MBPM
            .getMbpmBean().getPersistenceService();
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
        int result = category.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
