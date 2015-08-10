package br.net.mirante.singular.flow.core;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;
import br.net.mirante.singular.flow.core.view.GeradorDiagramaProcessoMBPM;
import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarService;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessDefinition<I extends ProcessInstance> implements Comparable<ProcessDefinition<?>> {

    private String grupo;

    private String sigla;

    private String nome;

    private FlowMap fluxo;

    private final Class<I> classeInstancia;

    private Class<? extends VariableWrapper> variableWrapperClass;

    private transient Constructor<I> construtor;

    private Serializable codDefinicao;

    private final Map<String, Serializable> mapaSiglaSituacaoCodSituacao = new HashMap<>();

    private EstrategiaPaginaInicio paginaInicial;

    private VarDefinitionMap<?> definicoes;

    private VarService varService;

    public ProcessDefinition(Class<I> classeInstancia) {
        this(classeInstancia, VarService.basic());
    }

    public ProcessDefinition(Class<I> classeInstancia, VarService varService) {
        this.classeInstancia = classeInstancia;
        this.varService = varService;
        getConstrutor();
    }

    public Class<I> getClasseInstancia() {
        return classeInstancia;
    }

    public void setVariableWrapperClass(Class<? extends VariableWrapper> variableWrapperClass) {
        this.variableWrapperClass = variableWrapperClass;
        if (variableWrapperClass != null) {
            if (!VariableEnabled.class.isAssignableFrom(classeInstancia)) {
                throw new RuntimeException("A classe " + classeInstancia.getName() + " não implementa " + VariableEnabled.class.getName()
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

    public Class<? extends VariableWrapper> getVariableWrapperClass() {
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

    public final synchronized FlowMap getFluxo() {
        if (fluxo == null) {
            FlowMap novo = createFluxo();
            if (novo.getDefinicaoProcesso() != this) {
                throw new RuntimeException("Mapa com definiçao trocada");
            }
            novo.validarConsistencia();
            fluxo = novo;
            MBPMUtil.adicionarIndeceDeOrdem(fluxo);
        }
        return fluxo;
    }

    protected abstract FlowMap createFluxo();

    public I recuperarInstancia(Integer pk) {
        IEntityProcessInstance dadosInstancia = getPersistenceService().recuperarInstanciaPorCod(pk);
        if (dadosInstancia != null) {
            return dadosToInstancia(dadosInstancia);
        }
        return null;
    }

    final MTask<?> retriveTask(IEntityProcessInstance demanda) {
        final IEntityTaskDefinition situacao = demanda.getSituacao();
        final MTask<?> task = getFluxo().getTaskWithSigla(situacao.getSigla());
        // Pode ser null se a demanda estiver em um estado no banco que não
        // corresponde a memoria
        return task;
    }

    public VarDefinitionMap<?> getVariaveis() {
        if (definicoes == null) {
            definicoes = getVarService().newVarDefinitionMap();
        }
        return definicoes;
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
                estadosAlvo.add(obterSituacaoPara(getFluxo().getTaskWithNome(situacao.getNome())));
            }
        }
        if (estadosAlvo.isEmpty()) {
            if (!exibirEncerradas) {
                for (IEntityTaskDefinition situacao : getDadosDefinicao().getSituacoes()) {
                    if (!situacao.isFim()) {
                        estadosAlvo.add(situacao);
                    }
                }
            }
        }
        return transformToInstancia(
                getPersistenceService().consultarInstanciasPorSituacao(getDadosDefinicao(), minDataInicio, maxDataInicio, estadosAlvo));
    }

    public final Collection<I> getTarefasAtivas() {
        return transformToInstancia(getPersistenceService().consultarInstanciasPorPessoaCriadora(getDadosDefinicao(), null, true));
    }

    public final Collection<I> getTarefasAtivasIniciadasPor(MUser pessoa) {
        Preconditions.checkNotNull(pessoa);
        return transformToInstancia(getPersistenceService().consultarInstanciasPorPessoaCriadora(getDadosDefinicao(), pessoa, true));
    }

    public final Collection<I> getTarefasEncerradas() {
        return transformToInstancia(getPersistenceService().consultarInstanciasPorPessoaCriadora(getDadosDefinicao(), null, false));
    }

    public final Collection<I> getTarefasEncerradasIniciadasPor(MUser pessoa) {
        Preconditions.checkNotNull(pessoa);
        return transformToInstancia(getPersistenceService().consultarInstanciasPorPessoaCriadora(getDadosDefinicao(), pessoa, false));
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesNotJava() {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformTo(getFluxo().getTasks().stream().filter(t -> !t.isJava())));
        estadosAlvo.addAll(transformTo(getFluxo().getEndTasks()));
        return (Set<X>) estadosAlvo;
    }

    public Collection<I> getInstanciasAtivasComPessoaOuEspera() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFluxo().getTasks().stream().filter(t -> t.isPeople() || t.isWait()));
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFluxo().getTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstancias(boolean exibirEncerradas) {
        final Set<IEntityTaskDefinition> estadosAlvo = new HashSet<>();
        estadosAlvo.addAll(transformTo(getFluxo().getTasks()));
        if (exibirEncerradas) {
            estadosAlvo.addAll(transformTo(getFluxo().getEndTasks()));
        }
        return getInstanciasPorEstado(estadosAlvo);
    }

    public Collection<I> getInstanciasAtivasComPessoa() {
        return getInstanciasPorEstado(getSituacoesComPessoa());
    }

    public <X extends IEntityTaskDefinition> Set<X> getSituacoesComPessoa() {
        return transformTo(getFluxo().getTasks().stream().filter(MTask::isPeople));
    }

    public Collection<I> getInstanciasInativas() {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(getFluxo().getEndTasks());
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasNoEstado(String... situacoesAlvo) {
        final Set<IEntityTaskDefinition> estadosAlvo = transformTo(situacoesAlvo);
        return getInstanciasPorEstado(estadosAlvo);
    }

    public List<I> getInstanciasPorEstado(Collection<? extends IEntityTaskDefinition> situacoesAlvo) {
        return transformToInstancia(getPersistenceService().consultarInstanciasPorSituacao(getDadosDefinicao(), null, null, situacoesAlvo));
    }

    final IEntityTaskDefinition getSituacaoInicial() {
        final MTask<?> inicial = getFluxo().getTaskInicial();
        return obterSituacaoPara(inicial);
    }

    protected final void setAtivo(boolean ativo) {
        IEntityProcess definicaoProcesso = getDadosDefinicao();
        if (definicaoProcesso.isAtivo() != ativo) {
            definicaoProcesso.setAtivo(ativo);
            getPersistenceService().atualizarDefinicao(definicaoProcesso);
        }
    }

    public final boolean isAtivo() {
        return getDadosDefinicao().isAtivo();
    }

    public IEntityProcess getDadosDefinicao() {
        if (codDefinicao == null) {
            IEntityProcess def = getPersistenceService().recuperarOuCriarDefinicaoProcesso(this);
            codDefinicao = def.getCod();
            return def;
        }
        final IEntityProcess def = getPersistenceService().recuperarDefinicaoProcessoPorCod(codDefinicao);

        if (def == null) {
            codDefinicao = null;
            throw criarErro("Definicao demanda incosistente com o BD: codigo não encontrado");
        } else if (!getSigla().equals(def.getSigla())) {
            codDefinicao = null;
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

    public IEntityTaskDefinition obterSituacaoPorNome(String taskName) {
        return obterSituacaoPara(getFluxo().getTaskWithNome(taskName));
    }

    public IEntityTaskDefinition obterSituacaoPorSigla(String sigla) {
        return obterSituacaoPara(getFluxo().getTaskWithSigla(sigla));
    }

    public IEntityTaskDefinition obterSituacaoPara(MTask<?> task) {
        if (task == null) {
            return null;
        }
        final Serializable codSituacao = mapaSiglaSituacaoCodSituacao.computeIfAbsent(task.getAbbreviation(),
                sigla -> getPersistenceService().recuperarOuCriarSituacaoInstancia(getDadosDefinicao(), task).getCod());

        IEntityTaskDefinition situacao = getPersistenceService().recuperarSituacaoInstanciaPorCod(codSituacao);
        if (situacao == null) {
            mapaSiglaSituacaoCodSituacao.clear();
            throw new RuntimeException("Dados inconsistentes com o BD");
        }
        return situacao;
    }

    protected int apagarFinalizadosAntigos() {
        final List<IEntityTaskDefinition> situacoes = new ArrayList<>();

        for (final MTask<?> task : getFluxo().getEndTasks()) {
            situacoes.add(obterSituacaoPara(task));
        }

        return getPersistenceService().apagarInstanciasProcesso(situacoes, getFluxo().getCleanupStrategy().getTime(),
                getFluxo().getCleanupStrategy().getTimeUnit());
    }

    protected List<I> transformToInstancia(List<? extends IEntityProcessInstance> demandas) {
        final List<I> nova = new ArrayList<>(demandas.size());
        for (final IEntityProcessInstance demanda : demandas) {
            nova.add(dadosToInstancia(demanda));
        }
        return nova;
    }

    public InputStream getImagem() {
        return GeradorDiagramaProcessoMBPM.gerarDiagrama(this);
    }

    protected final RuntimeException criarErro(String msg) {
        return new RuntimeException("Processo MBPM '" + getNome() + "': " + msg);
    }

    protected final RuntimeException criarErro(String msg, Exception e) {
        return new RuntimeException("Processo MBPM '" + getNome() + "': " + msg, e);
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
        int v = getGrupo().compareTo(dp2.getGrupo());
        if (v == 0) {
            v = getNome().compareTo(dp2.getNome());
        }
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessDefinition<?> that = (ProcessDefinition<?>) o;

        return grupo.equals(that.grupo) && nome.equals(that.nome);
    }

    @Override
    public int hashCode() {
        int result = grupo.hashCode();
        result = 31 * result + nome.hashCode();
        return result;
    }

    public final String getNome() {
        return nome;
    }

    public final String getSigla() {
        return sigla;
    }

    public final String getGrupo() {
        return grupo;
    }

    public final Lnk getPaginaInicial(MUser user) {
        return getPaginaInicial().getPaginaDestino(this, user);
    }

    public final EstrategiaPaginaInicio getPaginaInicial() {
        return paginaInicial;
    }

    public final void setPaginaInicial(EstrategiaPaginaInicio paginaInicial) {
        this.paginaInicial = paginaInicial;
    }

    public boolean isIniciavelPeloUsuario() {
        return paginaInicial != null;
    }

    public boolean isIniciavelPeloUsuario(MUser user) {
        return isIniciavelPeloUsuario();
    }

    protected final void setNome(String grupo, String nome) {
        setNome(grupo, calcularSigla(), nome);
    }

    final void setNome(String grupo, String sigla, String nome) {
        this.grupo = grupo;
        this.sigla = sigla;
        this.nome = nome;
    }

    final String calcularSigla() {
        String s = getClass().getSimpleName();
        if (!s.endsWith("Definicao")) {
            throw new RuntimeException("O nome da classe " + getClass().getName() + " deveria ter o sufixo 'Definicao'");
        }
        return s.substring(0, s.length() - "Definicao".length());
    }

    final IEntityProcessInstance criarDadosInstancia() {
        return getPersistenceService().criarInstancia(getDadosDefinicao(), getSituacaoInicial());
    }

    protected final IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance> getPersistenceService() {
        return (IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance>) MBPM
                .getMbpmBean().getPersistenceService();
    }

    protected VarService getVarService() {
        varService = varService.deserialize();
        return varService;
    }
}
