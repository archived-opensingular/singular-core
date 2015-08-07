package br.net.mirante.singular.flow.core;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessInstance {

    private final RefProcessDefinition definicao;

    private transient MTask<?> estadoAtual;

    private transient ExecucaoMTask execucaoTask;

    private transient IEntityProcessInstance demanda;

    private transient VarInstanceMap<?> variaveis;

    private transient VariableWrapper variableWrapper;

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> classeDefinicao) {
        definicao = RefProcessDefinition.loadByClass(classeDefinicao);
        demanda = getDefinicao().criarDadosInstancia();
    }

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> classeDefinicao, IEntityProcessInstance dadosInstanciaProcesso) {
        definicao = RefProcessDefinition.loadByClass(classeDefinicao);
        demanda = dadosInstanciaProcesso;
    }

    public <K extends ProcessDefinition<?>> K getDefinicao() {
        return (K) definicao.get();
    }

    private IEntityProcessInstance getDBInstance() {
        return demanda;
    }

    protected void setInstanciaPai(ProcessInstance pai) {
        getPersistenceService().definirInstanciaPai(getDBInstance(), pai.getDBInstance());

    }

    public <K extends ProcessInstance> K getInstanciaPai() {
        if (getDBInstance().getDemandaPai() != null) {
            return (K) MBPM.getMbpmBean().getInstancia(getDemanda().getDemandaPai());
        }
        return null;
    }

    public List<ProcessInstance> getInstanciasFilhas() {
        return getDBInstance().getDemandasFilhas().stream().map(MBPM.getMbpmBean()::getInstancia).collect(Collectors.toList());
    }

    public <I extends ProcessInstance, K extends ProcessDefinition<I>> List<I> getInstanciasFilhas(Class<K> classe) {
        return getStreamFilhas(classe).collect(Collectors.toList());
    }

    public <I extends ProcessInstance, K extends ProcessDefinition<I>> I getInstanciaFilha(Class<K> classe) {
        return getStreamFilhas(classe).findFirst().orElse(null);
    }

    private <I extends ProcessInstance, K extends ProcessDefinition<I>> Stream<I> getStreamFilhas(Class<K> classe) {
        K def = MBPM.getDefinicao(classe);
        IEntityProcess dadosDefinicaoProcesso = def.getDadosDefinicao();
        return getDBInstance().getDemandasFilhas().stream().filter(d -> d.getDefinicao().equals(dadosDefinicaoProcesso))
                .map(def::dadosToInstancia);
    }

    public TaskInstance getTarefaPai() {
        IEntityTaskInstance dbTaskInstance = getDBInstance().getTarefaPai();
        return dbTaskInstance == null ? null : MBPM.getInstanciaTarefa(dbTaskInstance);
    }

    public TaskInstance iniciar() {
        return iniciar(null);
    }

    public TaskInstance iniciar(VarInstanceMap<?> paramIn) {
        getDescricaoBD(); // Força a geração da descricação
        TaskInstance tarefaAtual = getTarefaPai();
        if (tarefaAtual == null && getDBInstance().getDemandaPai() != null) {
            tarefaAtual = MBPM.getMbpmBean().getInstancia(getDBInstance().getDemandaPai()).getTarefaAtual();
        }
        if (tarefaAtual != null) {
            tarefaAtual.log("Demanda Criada", getDescricaoExtendida(), null, MBPM.getUserSeDisponivel(), null, getDBInstance());
        }
        return EngineProcessamentoMBPM.iniciar(this, paramIn);
    }

    public void completarTarefa() {
        executarTransicao();
    }

    public void executarTransicao() {
        EngineProcessamentoMBPM.executarTransicao(this, null, null);
    }

    public void executarTransicao(String destino) {
        EngineProcessamentoMBPM.executarTransicao(this, destino, null);
    }

    public void executarTransicao(String destino, VarInstanceMap<?> param) {
        EngineProcessamentoMBPM.executarTransicao(this, destino, param);
    }

    public TransitionCall prepareTransition(String transitionName) {
        return getTarefaAtual().prepareTransition(transitionName);
    }

    protected final TaskInstance updateEstado(TaskInstance tarefaOrigem, MTransition transicaoOrigem, MTask<?> task, Date agora) {
        if (tarefaOrigem != null) {
            String siglaTransicao = null;
            if (transicaoOrigem != null) {
                siglaTransicao = transicaoOrigem.getName();
            }
            getPersistenceService().encerrarTarefaInstancia(tarefaOrigem.getEntityTaskInstance(), siglaTransicao, MBPM.getUserSeDisponivel());
        }
        IEntityTaskDefinition situacaoNova = getDefinicao().obterSituacaoPara(task);

        IEntityTaskInstance tarefa = getPersistenceService().adicionarTarefaInstancia(getDemanda(), situacaoNova);

        TaskInstance tarefaNova = getTarefa(tarefa);
        estadoAtual = task;

        MBPM.getMbpmBean().notifyStateUpdate(this);
        return tarefaNova;
    }

    public MTask<?> getEstado() {
        if (estadoAtual == null) {
            estadoAtual = getDefinicao().retriveTask(getDBInstance());
        }
        return estadoAtual;
    }

    public boolean isFim() {
        return getDemanda().getSituacao().isFim();
    }

    public boolean isNomeEstado(String nomeEstado) {
        final MTask<?> task = getDefinicao().getFluxo().getTaskWithNome(nomeEstado);
        if (task == null) {
            getDefinicao().getFluxo().createError("Não existe task com nome '" + nomeEstado + "'");
        }
        return getEstado().equals(task);
    }

    public boolean isSiglaEstado(String sigla) {
        final MTask<?> task = getDefinicao().getFluxo().getTaskWithSigla(sigla);
        if (task == null) {
            getDefinicao().getFluxo().createError("Não existe task com sigla '" + sigla + "'");
        }
        return getEstado().equals(task);
    }

    final void setContextoExecucao(ExecucaoMTask execucaoTask) {
        if (this.execucaoTask != null && execucaoTask != null) {
            throw criarErro("A instancia já está com um tarefa em processo de execução");
        }
        this.execucaoTask = execucaoTask;
    }

    public String getNomeProcesso() {
        return getDefinicao().getNome();
    }

    public String getNomeTarefa() {
        if (getEstado() != null) {
            return getEstado().getName();
        }
        TaskInstance tarefaAtual = getTarefaAtual();
        if (tarefaAtual != null) {
            // Uma situação legada, que não existe mais no fluxo mapeado
            return tarefaAtual.getEntityTaskInstance().getSituacao().getNome();
        }
        return null;
    }

    public final Lnk getHrefPadrao() {
        return MBPM.getHrefPadrao(this);
    }

    public Set<Integer> getRhComDireitoTarefa(String nomeTarefa) {
        return getDefinicao().getFluxo().getTaskPeopleWithSigla(nomeTarefa).getAccessStrategy().getFirstLevelUsersCodWithAccess(this);
    }

    public final String getTitulo() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getNomeProcesso());
        String d;
        if (getDefinicao().getFluxo().possuiMaisDeUmaTarefaVisivel()) {
            d = getNomeTarefa();
            if (d != null) {
                sb.append(" - ").append(d);
            }
        }
        d = getDescricao();
        if (d != null) {
            sb.append(" - ").append(d);
        }
        return sb.toString();
    }

    public final boolean possuiDireitoExecucao(MUser user) {
        if (getEstado() == null) {
            return false;
        }
        switch (getEstado().getTaskType()) {
            case People:
            case Wait:
                return (isAlocado(user.getCod()))
                        || (getEstrategiaAcesso() != null && getEstrategiaAcesso().canExecute(this, user));
            default:
                return false;
        }
    }

    public boolean possuiDireitoVisualizacao(MUser user) {
        switch (getDBInstance().getSituacao().getTipoTarefa()) {
            case People:
            case Wait:
                if (temAlocado() && isAlocado(user.getCod())) {
                    return true;
                }
            default:
        }
        return getEstrategiaAcesso() != null && getEstrategiaAcesso().canVisualize(this, user);
    }

    public Set<Integer> getFirstLevelUsersCodWithAccess() {
        return getEstrategiaAcesso().getFirstLevelUsersCodWithAccess(this);
    }

    public List<MUser> listAllocableUsers() {
        return getEstrategiaAcesso().listAllocableUsers(this);
    }

    protected final RuntimeException criarErro(String msg) {
        return MBPMUtil.generateError(this, msg);
    }

    @SuppressWarnings("rawtypes")
    private TaskAccessStrategy getEstrategiaAcesso() {
        return getEstado().getAccessStrategy();
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final void refresh() {
        getPersistenceService().refreshModel(getDBInstance());
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final <K extends IEntityProcessInstance> K getDemandaSemRefresh() {
        if (demanda.getCod() == null) {
            return salvarDemanda();
        }
        return (K) demanda;

    }

    public final <K extends IEntityProcessInstance> K getDemanda() {
        if (demanda.getCod() == null) {
            return salvarDemanda();
        }
        demanda = getPersistenceService().recuperarInstanciaPorCod(demanda.getCod());
        return (K) demanda;
    }

    public MUser getPessoaInteressada() {
        return getDBInstance().getPessoaCriadora();
    }

    public MUser getPessoaComSiglaPapel(String siglaPapel) {
        final IEntityRole dadosPapelInstancia = getDemanda().getPapelDemandaComSigla(siglaPapel);
        if (dadosPapelInstancia != null) {
            return dadosPapelInstancia.getPessoa();
        }
        return null;
    }

    public List<? extends IEntityRole> getRoles() {
        return getDemanda().getPapeis();
    }

    public void setPessoaCriadora(MUser pessoaCriadora) {
        getDBInstance().setPessoaCriadora(pessoaCriadora);
    }

    public MUser getPessoaCriadora() {
        return getDBInstance().getPessoaCriadora();
    }

    protected void setDescricao(String descricao) {
        getDBInstance().setDescricao(StringUtils.left(descricao, 250));
    }

    public final <K extends IEntityProcessInstance> K salvarDemanda() {
        demanda = getPersistenceService().salvarInstancia(demanda);
        return (K) demanda;
    }

    public final void forcarEstado(MTask<?> task) {
        final TaskInstance tarefaOrigem = getTarefaAtual();
        List<MUser> pessoasAnteriores = getResponsaveisDiretos();
        final Date agora = new Date();
        TaskInstance tarefaNova = updateEstado(tarefaOrigem, null, task, agora);
        if (tarefaOrigem != null) {
            tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getNome() + "' para '" + task.getName() + "'",
                    null, MBPM.getUserSeDisponivel(), agora).sendEmail(pessoasAnteriores);
        }

        ExecucaoMTask execucaoMTask = new ExecucaoMTask(this, tarefaNova, null);
        task.notifyTaskStart(getTarefaMaisRecenteComNome(task.getName()), execucaoMTask);
    }

    public Date getDataAlvoFim() {
        final IEntityTaskInstance tarefa = getDBInstance().getTarefaAtiva();
        if (tarefa != null) {
            return tarefa.getDataAlvoFim();
        }
        return null;
    }

    public Date getDataInicio() {
        return getDBInstance().getDataInicio();
    }

    public Date getDataFim() {
        return getDBInstance().getDataFim();
    }

    public Integer getCod() {
        return demanda.getCod();
    }

    public String getId() {
        return demanda.getCod().toString();
    }

    public String getFullId() {
        return MBPM.generateID(this);
    }

    public TaskInstance getTarefa(final IEntityTaskInstance tarefa) {
        return tarefa != null ? new TaskInstance(this, tarefa) : null;
    }

    /**
     * O mesmo que getDescricaoCompleta.
     */
    public String getDescricao() {
        return getDescricaoCompleta();
    }

    /**
     * Nome do processo seguido da descrição completa.
     */
    public final String getDescricaoExtendida() {
        String descricao = getDescricao();
        if (descricao == null) {
            return getNomeProcesso();
        }
        return getNomeProcesso() + " - " + descricao;
    }

    protected final String getDescricaoBD() {
        String descricao = getDBInstance().getDescricao();
        if (descricao == null) {
            descricao = criarDescricaoInicial();
            if (!StringUtils.isBlank(descricao)) {
                setDescricao(descricao);
            }
        }
        return descricao;
    }

    /**
     * Cria a descrição que vai gravada no banco de dados. Deve ser sobreescrito
     * para ter efeito
     */
    protected String criarDescricaoInicial() {
        return null;
    }

    /**
     * Sobrescreve a descrição da demanda a partir do método
     * {@link #criarDescricaoInicial()}
     *
     * @return true caso tenha sido alterada a descrição
     */
    public final boolean recriarDescricaoInicial() {
        String descricao = criarDescricaoInicial();
        if (!StringUtils.isBlank(descricao) && !descricao.equalsIgnoreCase(getDBInstance().getDescricao())) {
            setDescricao(descricao);
            return true;
        }
        return false;
    }

    /**
     * Cria versão extendida da descrição em relação ao campo descrição no BD.
     * Geralmente são adicionadas informações que não precisam ter cache feito
     * em banco de dados.
     */
    protected String getDescricaoCompleta() {
        return getDescricaoBD();
    }

    public String getNumeroDisplay() {
        return getDemanda().getCod().toString();
    }

    public List<MUser> getResponsaveisDiretos() {
        TaskInstance tarefa = getTarefaAtual();
        if (tarefa != null) {
            return tarefa.getDirectlyResponsible();
        }
        return Collections.emptyList();
    }

    private final void addPapelPessoa(MProcessRole mPapel, MUser pessoa) {
        if (getPessoaComSiglaPapel(mPapel.getAbbreviation()) == null) {
            getPersistenceService().definirPapelPessoa(getDefinicao().getDadosDefinicao(), getDemanda(), mPapel.getAbbreviation(), pessoa);
        }
    }

    public final void addOrReplacePapelPessoa(final String siglaPapel, MUser novaPessoa) {
        MProcessRole papel = getDefinicao().getFluxo().getRoleWithAbbreviation(siglaPapel);
        MUser pessoaAnterior = getPessoaComSiglaPapel(papel.getAbbreviation());

        if (pessoaAnterior == null) {
            if (novaPessoa != null) {
                addPapelPessoa(papel, novaPessoa);
                getDefinicao().getFluxo().notifyRoleChange(this, papel, pessoaAnterior, novaPessoa);

                final TaskInstance tarefaMaisRecente = getTarefaMaisRecente();
                if (tarefaMaisRecente != null) {
                    tarefaMaisRecente.log("Papel definido", String.format("%s: %s", papel.getName(), novaPessoa.getNomeGuerra()));
                }
            }
        } else if (novaPessoa == null || !pessoaAnterior.equals(novaPessoa)) {
            getPersistenceService().removerPapelPessoa(getDemanda(), papel.getAbbreviation());
            if (novaPessoa != null) {
                addPapelPessoa(papel, novaPessoa);
            }

            getDefinicao().getFluxo().notifyRoleChange(this, papel, pessoaAnterior, novaPessoa);
            final TaskInstance tarefaMaisRecente = getTarefaMaisRecente();
            if (tarefaMaisRecente != null) {
                if (novaPessoa != null) {
                    tarefaMaisRecente.log("Papel alterado", String.format("%s: %s", papel.getName(), novaPessoa.getNomeGuerra()));
                } else {
                    tarefaMaisRecente.log("Papel removido", papel.getName());
                }
            }
        }
    }

    public void setVariavel(String nomeVariavel, Object valor) {
        getVariaveis().setValor(nomeVariavel, valor);
    }

    public final void setVariables(VariableWrapper newVariableSet) {
        getDefinicao().verifyVariableWrapperClass(newVariableSet.getClass());
        getVariaveis().addValues(newVariableSet.getVariables(), true);
    }

    public final Date getValorVariavelData(String nomeVariavel) {
        return getVariaveis().getValorData(nomeVariavel);
    }

    public final Boolean getValorVariavelBoolean(String nomeVariavel) {
        return getVariaveis().getValorBoolean(nomeVariavel);
    }

    public final String getValorVariavelString(String nomeVariavel) {
        return getVariaveis().getValorString(nomeVariavel);
    }

    public final Integer getValorVariavelInteger(String nomeVariavel) {
        return getVariaveis().getValorInteger(nomeVariavel);
    }

    public final <T> T getValorVariavel(String nomeVariavel) {
        return getVariaveis().getValor(nomeVariavel);
    }

    public final VarInstanceMap<?> getVariaveis() {
        if (variaveis == null) {
            variaveis = new VarInstanceTableProcess(this);
        }
        return variaveis;
    }

    protected void validarPreInicio() {
        if (variaveis == null && !getDefinicao().getVariaveis().hasRequired()) {
            return;
        }

        ValidationResult result = getVariaveis().validar();
        if (result.hasErros()) {
            throw criarErro("Erro ao iniciar processo '" + getNomeProcesso() + "': " + result);
        }
    }

    public boolean temAlocado() {
        return getDemanda().getTarefas().stream().anyMatch(tarefa -> isTarefaAtiva(tarefa) && tarefa.getPessoaAlocada() != null);
    }

    public boolean isAlocado(Integer codPessoa) {
        return getDemanda()
                .getTarefas()
                .stream()
                .anyMatch(
                        tarefa -> isTarefaAtiva(tarefa) && tarefa.getPessoaAlocada() != null
                                && tarefa.getPessoaAlocada().getCod().equals(codPessoa));
    }

    public List<TaskInstance> getTarefas() {
        IEntityProcessInstance demanda = getDemanda();
        return demanda.getTarefas().stream().map(this::getTarefa).collect(Collectors.toList());
    }

    private TaskInstance procurarUmFiltroPorDadosTarefa(boolean procuraAPartirDoFim, Predicate<IEntityTaskInstance> condicao) {
        List<? extends IEntityTaskInstance> lista = getDemanda().getTarefas();
        if (procuraAPartirDoFim) {
            lista = Lists.reverse(lista);
        }

        return getTarefa(lista.stream().filter(condicao).findFirst().orElse(null));
    }

    private static boolean isTarefaAtiva(IEntityTaskInstance tarefa) {
        return tarefa.getDataFim() == null;
    }

    public TaskInstance getUltimaTarefa() {
        return procurarUmFiltroPorDadosTarefa(true, t -> true);
    }

    public TaskInstance getTarefaAtual() {
        return procurarUmFiltroPorDadosTarefa(true, ProcessInstance::isTarefaAtiva);
    }

    public TaskInstance getTarefaMaisRecenteComNome(final String nomeTipo) {
        return procurarUmFiltroPorDadosTarefa(true, tarefa -> tarefa.getSituacao().getNome().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getTarefaMaisRecente() {
        return procurarUmFiltroPorDadosTarefa(true, tarefa -> true);
    }

    public TaskInstance getUltimaTarefaConcluidaComNome(final String nomeTipo) {
        return procurarUmFiltroPorDadosTarefa(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getNome().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getUltimaTarefaConcluidaTipo(final MTask<?> tipo) {
        return procurarUmFiltroPorDadosTarefa(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getSigla().equalsIgnoreCase(tipo.getAbbreviation()));
    }

    public TaskInstance getUltimaTarefaConcluida(final TaskType tipoTarefa) {
        return procurarUmFiltroPorDadosTarefa(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getTipoTarefa().equals(tipoTarefa));
    }

    protected IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance> getPersistenceService() {
        return getDefinicao().getPersistenceService();
    }

    protected final <T extends VariableWrapper> T getVariablesWrapper(Class<T> variableWrapperClass) {
        if (variableWrapper == null) {
            if (variableWrapperClass != getDefinicao().getVariableWrapperClass()) {
                throw new RuntimeException("A classe do parâmetro (" + variableWrapperClass.getName() + ") é diferente da definida em "
                        + getDescricao().getClass().getName() + ". A definição do processo informou o wrapper como sendo "
                        + getDefinicao().getVariableWrapperClass());
            }
            try {
                variableWrapper = variableWrapperClass.getConstructor().newInstance();
                variableWrapper.setVariables(getVariaveis());
            } catch (Exception e) {
                throw new RuntimeException("Erro instanciando variableWrapper: " + e.getMessage(), e);
            }
        }
        return variableWrapperClass.cast(variableWrapper);
    }
}
