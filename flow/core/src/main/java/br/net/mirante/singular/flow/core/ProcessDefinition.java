package br.net.mirante.singular.flow.core;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDataService;
import br.net.mirante.singular.flow.core.view.GeradorDiagramaProcessoMBPM;
import br.net.mirante.singular.flow.util.props.PropRef;
import br.net.mirante.singular.flow.util.props.Props;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

@SuppressWarnings({ "serial", "unchecked" })
public abstract class ProcessDefinition<I extends ProcessInstance> implements Comparable<ProcessDefinition<?>> {

    private final Class<I> instanceClass;

    private String category;

    private String abbreviation;

    private String name;

    private FlowMap flowMap;

    private Serializable entityCod;

    private final Map<String, Serializable> entityCodByTaskAbbreviation = new HashMap<>();

    private IProcessCreationPageStrategy creationPage;

    private Class<? extends VariableWrapper> variableWrapperClass;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

    private IProcessDataService<I> processDataService;

    private Props properties;

    private transient Constructor<I> construtor;

    protected ProcessDefinition(Class<I> instanceClass) {
        this(instanceClass, VarService.basic());
    }

    protected ProcessDefinition(Class<I> instanceClass, VarService varService) {
        this.instanceClass = instanceClass;
        this.variableService = varService;
        getConstrutor();
    }

    public final Class<I> getClasseInstancia() {
        return instanceClass;
    }

    protected final void setVariableWrapperClass(Class<? extends VariableWrapper> variableWrapperClass) {
        this.variableWrapperClass = variableWrapperClass;
        if (variableWrapperClass != null) {
            if (!VariableEnabled.class.isAssignableFrom(instanceClass)) {
                throw new RuntimeException("A classe " + instanceClass.getName() + " não implementa " + VariableEnabled.class.getName()
                    + " sendo que a definição do processo (" + getClass().getName() + ") trabalha com variáveis.");
            }
            newVariableWrapper(variableWrapperClass).configVariables(getVariables());
        }
    }

    private static <T extends VariableWrapper> T newVariableWrapper(Class<T> variableWrapperClass) {
        try {
            return variableWrapperClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erro instanciando " + variableWrapperClass.getName(), e);
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
            throw new RuntimeException(getClass().getName() + " espera que as variáveis sejam do tipo " + variableWrapperClass);
        }
    }

    protected abstract FlowMap createFlowMap();

    public final FlowMap getFlowMap() {
        if (flowMap == null) {
            synchronized (this) {
                if (flowMap == null) {
                    FlowMap novo = createFlowMap();

                    Preconditions.checkArgument(novo.getProcessDefinition() == this, "Mapa com definiçao trocada");

                    novo.verifyConsistency();
                    flowMap = novo;
                    MBPMUtil.calculateTaskOrder(flowMap);
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

    protected final void setActive(boolean ativo) {
        IEntityProcess definicaoProcesso = getEntity();
        if (definicaoProcesso.isAtivo() != ativo) {
            definicaoProcesso.setAtivo(ativo);
            getPersistenceService().updateProcessDefinition(definicaoProcesso);
        }
    }

    public final boolean isActive() {
        return getEntity().isAtivo();
    }

    public <T> T getProperty(PropRef<T> propRef, T defaultValue) {
        return properties == null ? defaultValue : MoreObjects.firstNonNull(getProperties().get(propRef), defaultValue);
    }

    public <T> T getProperty(PropRef<T> propRef) {
        return properties == null ? null : getProperties().get(propRef);
    }

    protected <T> ProcessDefinition<I> setProperty(PropRef<T> propRef, T value) {
        getProperties().set(propRef, value);
        return this;
    }

    public final IEntityProcess getEntity() {
        if (entityCod == null) {
            IEntityProcess def = getPersistenceService().retrieveOrCreateProcessDefinitionFor(this);
            entityCod = def.getCod();
            return def;
        }
        final IEntityProcess def = getPersistenceService().retrieveProcessDefinitionByCod(entityCod);

        if (def == null) {
            entityCod = null;
            throw criarErro("Definicao demanda incosistente com o BD: codigo não encontrado");
        } else if (!getAbbreviation().equals(def.getSigla())) {
            entityCod = null;
            throw criarErro("Definicao demanda incosistente com o BD: sigla recuperada diferente");
        }

        return def;
    }

    public final <X extends IEntityTaskDefinition> Set<X> getEntityNotJavaTask() {
        return convertToEntityTaskDefinition(getFlowMap().getAllTasks().stream().filter(t -> !t.isJava()));
    }

    public final <X extends IEntityTaskDefinition> Set<X> getEntityPeopleTasks() {
        return convertToEntityTaskDefinition(getFlowMap().getPeopleTasks().stream());
    }

    public final IEntityTaskDefinition getEntityStartTask() {
        final MTask<?> inicial = getFlowMap().getStartTask();
        return getEntityTask(inicial);
    }
    
    public final IEntityTaskDefinition getEntityTaskWithName(String taskName) {
        return getEntityTask(getFlowMap().getTaskWithName(taskName));
    }

    public final IEntityTaskDefinition getEntityTaskWithAbbreviation(String sigla) {
        return getEntityTask(getFlowMap().getTaskWithAbbreviation(sigla));
    }

    public final IEntityTaskDefinition getEntityTask(MTask<?> task) {
        if (task == null) {
            return null;
        }
        final Serializable codSituacao = entityCodByTaskAbbreviation.computeIfAbsent(task.getAbbreviation(),
            sigla -> getPersistenceService().retrieveOrCreateStateFor(getEntity(), task).getCod());

        IEntityTaskDefinition situacao = getPersistenceService().retrieveTaskStateByCod(codSituacao);
        if (situacao == null) {
            entityCodByTaskAbbreviation.clear();
            throw new RuntimeException("Dados inconsistentes com o BD");
        }
        return situacao;
    }

    public InputStream getFlowImage() {
        return GeradorDiagramaProcessoMBPM.gerarDiagrama(this);
    }

    protected final RuntimeException criarErro(String msg) {
        return new RuntimeException("Processo MBPM '" + getName() + "': " + msg);
    }

    protected final RuntimeException criarErro(String msg, Exception e) {
        return new RuntimeException("Processo MBPM '" + getName() + "': " + msg, e);
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

    protected final void setName(String category, String name) {
        setName(category, generateAbbreviation(), name);
    }

    private void setName(String category, String abbreviation, String name) {
        this.category = category;
        this.abbreviation = abbreviation;
        this.name = name;
    }

    private String generateAbbreviation() {
        String className = getClass().getSimpleName();
        if (!className.endsWith("Definicao")) {
            throw new RuntimeException("O nome da classe " + getClass().getName() + " deveria ter o sufixo 'Definicao'");
        }
        return className.substring(0, className.length() - "Definicao".length());
    }

    final Props getProperties() {
        if (properties == null) {
            properties = new Props();
        }
        return properties;
    }

    final Set<IEntityTaskDefinition> convertToEntityTaskDefinition(Collection<? extends MTask<?>> collection) {
        return convertToEntityTaskDefinition(collection.stream());
    }

    final <X extends IEntityTaskDefinition> Set<X> convertToEntityTaskDefinition(Stream<? extends MTask<?>> stream) {
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
                for (Constructor<?> constructor : getClasseInstancia().getConstructors()) {
                    if (constructor.getParameterTypes().length == 1
                        && IEntityProcessInstance.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                        this.construtor = (Constructor<I>) constructor;
                    }
                }
                Objects.requireNonNull(this.construtor);
            } catch (final Exception e) {
                throw criarErro("Construtor ausente: " + getClasseInstancia().getName() + "(" + IEntityProcessInstance.class.getName() + ")",
                    e);
            }
        }
        return construtor;
    }

    final IEntityProcessInstance createProcessInstance() {
        return getPersistenceService().createProcessInstance(getEntity(), getEntityStartTask());
    }

    final IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole>) MBPM
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
