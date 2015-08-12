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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

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
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;
import br.net.mirante.singular.flow.util.view.Lnk;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessDefinition<I extends ProcessInstance> implements Comparable<ProcessDefinition<?>> {

    private final Class<I> instanceClass;

    private String category;

    private String abbreviation;

    private String name;

    private FlowMap flowMap;

    private Serializable entityCod;

    private final Map<String, Serializable> mapaSiglaSituacaoCodSituacao = new HashMap<>();

    private IProcessCreationPageStrategy creationPage;

    private Class<? extends VariableWrapper> variableWrapperClass;

    private VarDefinitionMap<?> variableDefinitions;

    private VarService variableService;

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
            newVariableWrapper(variableWrapperClass).configVariables(getVariaveis());
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
            FlowMap novo = createFluxo();
            if (novo.getProcessDefinition() != this) {
                throw new RuntimeException("Mapa com definiçao trocada");
            }
            novo.verifyConsistency();
            flowMap = novo;
            MBPMUtil.calculateTaskOrder(flowMap);
        }
        return flowMap;
    }

    protected abstract FlowMap createFluxo();

    public I recuperarInstancia(Integer pk) {
        IEntityProcessInstance dadosInstancia = getPersistenceService().retrieveProcessInstanceByCod(pk);
        if (dadosInstancia != null) {
            return dadosToInstancia(dadosInstancia);
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

    public VarDefinitionMap<?> getVariaveis() {
        if (variableDefinitions == null) {
            variableDefinitions = getVarService().newVarDefinitionMap();
        }
        return variableDefinitions;
    }

    protected final I dadosToInstancia(IEntityProcessInstance dadosInstancia) {
        Preconditions.checkNotNull(dadosInstancia);
        try {
            return getConstrutor().newInstance(dadosInstancia);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public List<I> getInstanciasNoEstado(MTask<?> task) {
        final IEntityTaskDefinition obterSituacaoPara = obterSituacaoPara(task);
        return getInstanciasPorEstado(obterSituacaoPara != null ? Sets.newHashSet(obterSituacaoPara) : null);
    }

    public List<I> getTarefasPorNomeSituacao(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas, String... situacoesAlvo) {
        Set<IEntityTaskDefinition> situacoes = transformTo(situacoesAlvo);
        return getTarefasPorSituacao(minDataInicio, maxDataInicio, exibirEncerradas,
                situacoes.toArray(new IEntityTaskDefinition[situacoes.size()]));
    }

    public List<I> getTarefasPorSituacao(Date minDataInicio, Date maxDataInicio, boolean exibirEncerradas,
            IEntityTaskDefinition... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        for (final IEntityTaskDefinition situacao : situacoesAlvo) {
            if (situacao != null) {
                estadosAlvo.add(obterSituacaoPara(getFlowMap().getTaskWithName(situacao.getNome())));
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
        return transformToInstance(
                getPersistenceService().retrieveProcessInstancesWith(getEntity(), minDataInicio, maxDataInicio, estadosAlvo));
    }

    public final Collection<I> getTarefasAtivas() {
        return transformToInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, true));
    }

    public final Collection<I> getTarefasAtivasIniciadasPor(MUser pessoa) {
        Preconditions.checkNotNull(pessoa);
        return transformToInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), pessoa, true));
    }

    public final Collection<I> getTarefasEncerradas() {
        return transformToInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, false));
    }

    public final Collection<I> getTarefasEncerradasIniciadasPor(MUser pessoa) {
        Preconditions.checkNotNull(pessoa);
        return transformToInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), pessoa, false));
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesNotJava() {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformTo(getFlowMap().getTasks().stream().filter(t -> !t.isJava())));
        estadosAlvo.addAll(transformTo(getFlowMap().getEndTasks()));
        return (Set<X>) estadosAlvo;
    }

    public Collection<I> getInstanciasAtivasComPessoaOuEspera() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFlowMap().getTasks().stream().filter(t -> t.isPeople() || t.isWait()));
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFlowMap().getTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstancias(boolean exibirEncerradas) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformTo(getFlowMap().getTasks()));
        if (exibirEncerradas) {
            estadosAlvo.addAll(transformTo(getFlowMap().getEndTasks()));
        }
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivasComPessoa() {
        return getInstanciasPorEstado(getSituacoesComPessoa());
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesComPessoa() {
        return transformTo(getFlowMap().getTasks().stream().filter(MTask::isPeople));
    }

    public Collection<I> getInstanciasInativas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFlowMap().getEndTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasNoEstado(String... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(situacoesAlvo);
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasPorEstado(Collection<? extends IEntityTaskDefinition> situacoesAlvo) {
        return transformToInstance(getPersistenceService().retrieveProcessInstancesWith(getEntity(), null, null, situacoesAlvo));
    }

    final IEntityTaskDefinition getSituacaoInicial() {
        final MTask<?> inicial = getFlowMap().getStartTask();
        return obterSituacaoPara(inicial);
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
                Preconditions.checkNotNull(this.construtor);
            } catch (final Exception e) {
                throw criarErro("Construtor ausente: " + getClasseInstancia().getName() + "(" + IEntityProcessInstance.class.getName() + ")",
                        e);
            }
        }
        return construtor;
    }

    public final IEntityTaskDefinition obterSituacaoPorNome(String taskName) {
        return obterSituacaoPara(getFlowMap().getTaskWithName(taskName));
    }

    public final IEntityTaskDefinition obterSituacaoPorSigla(String sigla) {
        return obterSituacaoPara(getFlowMap().getTaskWithAbbreviation(sigla));
    }

    public final IEntityTaskDefinition obterSituacaoPara(MTask<?> task) {
        if (task == null) {
            return null;
        }
        final Serializable codSituacao = mapaSiglaSituacaoCodSituacao.computeIfAbsent(task.getAbbreviation(),
                sigla -> getPersistenceService().retrieveOrCreateStateFor(getEntity(), task).getCod());

        IEntityTaskDefinition situacao = getPersistenceService().retrieveTaskStateByCod(codSituacao);
        if (situacao == null) {
            mapaSiglaSituacaoCodSituacao.clear();
            throw new RuntimeException("Dados inconsistentes com o BD");
        }
        return situacao;
    }

    protected List<I> transformToInstance(List<? extends IEntityProcessInstance> demandas) {
        return demandas.stream().map(this::dadosToInstancia).collect(Collectors.toList());
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

    private Set<IEntityTaskDefinition> transformTo(Collection<? extends MTask<?>> collection) {
        return transformTo(collection.stream());
    }

    private <X extends IEntityTaskDefinition> Set<X> transformTo(Stream<? extends MTask<?>> stream) {
        return (Set<X>) stream.map(this::obterSituacaoPara).collect(Collectors.toSet());
    }

    private Set<IEntityTaskDefinition> transformTo(String... situacoesAlvo) {
        return Arrays.stream(situacoesAlvo).map(this::obterSituacaoPorNome).collect(Collectors.toSet());
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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

    protected final void setNome(String grupo, String nome) {
        setNome(grupo, calcularSigla(), nome);
    }

    final void setNome(String grupo, String sigla, String nome) {
        this.category = grupo;
        this.abbreviation = sigla;
        this.name = nome;
    }

    final String calcularSigla() {
        String s = getClass().getSimpleName();
        if (!s.endsWith("Definicao")) {
            throw new RuntimeException("O nome da classe " + getClass().getName() + " deveria ter o sufixo 'Definicao'");
        }
        return s.substring(0, s.length() - "Definicao".length());
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
