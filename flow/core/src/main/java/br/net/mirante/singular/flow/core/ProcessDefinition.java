package br.net.mirante.singular.flow.core;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;
import br.net.mirante.singular.flow.core.view.GeradorDiagramaProcessoMBPM;
import br.net.mirante.singular.flow.util.props.PropRef;
import br.net.mirante.singular.flow.util.props.Props;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

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

    private Props properties;

    private transient Constructor<I> construtor;

    protected ProcessDefinition(Class<I> classeInstancia) {
        this(classeInstancia, VarService.basic());
    }

    protected ProcessDefinition(Class<I> classeInstancia, VarService varService) {
        this.instanceClass = classeInstancia;
        this.variableService = varService;
        getConstrutor();
    }

    public final Class<I> getClasseInstancia() {
        return instanceClass;
    }

    public final void setVariableWrapperClass(Class<? extends VariableWrapper> variableWrapperClass) {
        this.variableWrapperClass = variableWrapperClass;
        if (variableWrapperClass != null) {
            if (!VariableEnabled.class.isAssignableFrom(instanceClass)) {
                throw new RuntimeException("A classe " + instanceClass.getName() + " não implementa " + VariableEnabled.class.getName()
                    + " sendo que a definição do processo (" + getClass().getName() + ") trabalha com variáveis.");
            }
            // for( Type i : classeInstancia.getGenericInterfaces()) {
            // if (i instanceof Class && VariableEnabled.class.) {
            //
            // }
            // }
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

    public final synchronized FlowMap getFlowMap() {
        if (flowMap == null) {
            FlowMap novo = createFlowMap();
            if (novo.getProcessDefinition() != this) {
                throw new RuntimeException("Mapa com definiçao trocada");
            }
            novo.verifyConsistency();
            flowMap = novo;
            MBPMUtil.calculateTaskOrder(flowMap);
        }
        return flowMap;
    }

    protected abstract FlowMap createFlowMap();

    public I retrieveProcessInstance(Integer entityCod) {
        IEntityProcessInstance entityProcessInstance = getPersistenceService().retrieveProcessInstanceByCod(entityCod);
        if (entityProcessInstance != null) {
            return convertToProcessInstance(entityProcessInstance);
        }
        return null;
    }

    final MTask<?> retriveTask(IEntityProcessInstance demanda) {
        final IEntityTaskDefinition situacao = demanda.getSituacao();
        final MTask<?> task = getFlowMap().getTaskWithAbbreviation(situacao.getSigla());
        // Pode ser null se a demanda estiver em um estado no banco que não
        // corresponde a memoria
        return task;
    }

    public VarDefinitionMap<?> getVariables() {
        if (variableDefinitions == null) {
            variableDefinitions = getVarService().newVarDefinitionMap();
        }
        return variableDefinitions;
    }

    protected final I convertToProcessInstance(IEntityProcessInstance dadosInstancia) {
        Objects.requireNonNull(dadosInstancia);
        try {
            return getConstrutor().newInstance(dadosInstancia);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public List<I> getInstanciasNoEstado(MTask<?> task) {
        final IEntityTaskDefinition obterSituacaoPara = getEntityTask(task);
        return getInstanciasPorEstado(obterSituacaoPara != null ? Sets.newHashSet(obterSituacaoPara) : null);
    }

    public List<I> getTarefasPorNomeSituacao(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas, String... situacoesAlvo) {
        Set<IEntityTaskDefinition> situacoes = transformToEntityTaskDefinition(situacoesAlvo);
        return getTarefasPorSituacao(minDataInicio, maxDataInicio, exibirEncerradas,
            situacoes.toArray(new IEntityTaskDefinition[situacoes.size()]));
    }

    public List<I> getTarefasPorSituacao(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas,
        IEntityTaskDefinition... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        for (final IEntityTaskDefinition situacao : situacoesAlvo) {
            if (situacao != null) {
                estadosAlvo.add(getEntityTask(getFlowMap().getTaskWithName(situacao.getNome())));
            }
        }
        if (estadosAlvo.isEmpty()) {
            if (!exibirEncerradas) {
                for (IEntityTaskDefinition situacao : getEntity().getSituacoes()) {
                    if (!situacao.isFim()) {
                        estadosAlvo.add(situacao);
                    }
                }
            }
        }
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), minDataInicio, maxDataInicio, estadosAlvo));
    }

    public final Collection<I> getTarefasAtivas() {
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, true));
    }

    public final Collection<I> getTarefasAtivasIniciadasPor(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), pessoa, true));
    }

    public final Collection<I> getTarefasEncerradas() {
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, false));
    }

    public final Collection<I> getTarefasEncerradasIniciadasPor(MUser pessoa) {
        Objects.requireNonNull(pessoa);
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), pessoa, false));
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesNotJava() {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformToEntityTaskDefinition(getFlowMap().getTasks().stream().filter(t -> !t.isJava())));
        estadosAlvo.addAll(transformToEntityTaskDefinition(getFlowMap().getEndTasks()));
        return (Set<X>) estadosAlvo;
    }

    public Collection<I> getInstanciasAtivasComPessoaOuEspera() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformToEntityTaskDefinition(getFlowMap().getTasks().stream().filter(t -> t.isPeople() || t.isWait()));
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformToEntityTaskDefinition(getFlowMap().getTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstancias(boolean exibirEncerradas) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformToEntityTaskDefinition(getFlowMap().getTasks()));
        if (exibirEncerradas) {
            estadosAlvo.addAll(transformToEntityTaskDefinition(getFlowMap().getEndTasks()));
        }
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivasComPessoa() {
        return getInstanciasPorEstado(getSituacoesComPessoa());
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesComPessoa() {
        return transformToEntityTaskDefinition(getFlowMap().getTasks().stream().filter(MTask::isPeople));
    }

    public Collection<I> getInstanciasInativas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformToEntityTaskDefinition(getFlowMap().getEndTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasNoEstado(String... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = transformToEntityTaskDefinition(situacoesAlvo);
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasPorEstado(Collection<? extends IEntityTaskDefinition> situacoesAlvo) {
        return transformToProcessInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, null, situacoesAlvo));
    }

    final IEntityTaskDefinition getSituacaoInicial() {
        final MTask<?> inicial = getFlowMap().getStartTask();
        return getEntityTask(inicial);
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
    
    Props getProperties() {
        if (properties == null) {
            properties = new Props();
        }
        return properties;
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

    protected List<I> transformToProcessInstance(List<? extends IEntityProcessInstance> demandas) {
        return demandas.stream().map(this::convertToProcessInstance).collect(Collectors.toList());
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

    private Set<IEntityTaskDefinition> transformToEntityTaskDefinition(Collection<? extends MTask<?>> collection) {
        return transformToEntityTaskDefinition(collection.stream());
    }

    private <X extends IEntityTaskDefinition> Set<X> transformToEntityTaskDefinition(Stream<? extends MTask<?>> stream) {
        return (Set<X>) stream.map(this::getEntityTask).collect(Collectors.toSet());
    }

    private Set<IEntityTaskDefinition> transformToEntityTaskDefinition(String... situacoesAlvo) {
        return Arrays.stream(situacoesAlvo).map(this::getEntityTaskWithName).collect(Collectors.toSet());
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

    public final void setCreationPageStrategy(IProcessCreationPageStrategy creationPage) {
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

    final IEntityProcessInstance createProcessInstance() {
        return getPersistenceService().createProcessInstance(getEntity(), getSituacaoInicial());
    }

    protected final IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole>) MBPM
            .getMbpmBean().getPersistenceService();
    }

    protected VarService getVarService() {
        variableService = variableService.deserialize();
        return variableService;
    }
}
