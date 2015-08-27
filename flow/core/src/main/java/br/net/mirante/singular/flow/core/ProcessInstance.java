package br.net.mirante.singular.flow.core;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.collect.Lists;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessInstance {

    private final RefProcessDefinition processDefinitionRef;

    private transient MTask<?> estadoAtual;

    private transient ExecucaoMTask execucaoTask;

    private transient IEntityProcessInstance entity;

    private transient VarInstanceMap<?> variables;

    private transient VariableWrapper variableWrapper;

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> definitionClass) {
        processDefinitionRef = RefProcessDefinition.loadByClass(definitionClass);
        entity = getDefinicao().createProcessInstance();
    }

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> definitionClass, IEntityProcessInstance entityProcessInstance) {
        processDefinitionRef = RefProcessDefinition.loadByClass(definitionClass);
        entity = entityProcessInstance;
    }

    public <K extends ProcessDefinition<?>> K getDefinicao() {
        return (K) processDefinitionRef.get();
    }

    private IEntityProcessInstance getInternalEntity() {
        return entity;
    }

    protected void setParent(ProcessInstance pai) {
        getPersistenceService().setProcessInstanceParent(getInternalEntity(), pai.getInternalEntity());
    }

    public <K extends ProcessInstance> K getInstanciaPai() {
        if (getInternalEntity().getDemandaPai() != null) {
            return (K) MBPM.getMbpmBean().getProcessInstance(getEntity().getDemandaPai());
        }
        return null;
    }

    public List<ProcessInstance> getInstanciasFilhas() {
        return getInternalEntity().getDemandasFilhas().stream().map(MBPM.getMbpmBean()::getProcessInstance).collect(Collectors.toList());
    }

    public <I extends ProcessInstance, K extends ProcessDefinition<I>> List<I> getInstanciasFilhas(Class<K> classe) {
        return getStreamFilhas(classe).collect(Collectors.toList());
    }

    public <I extends ProcessInstance, K extends ProcessDefinition<I>> I getInstanciaFilha(Class<K> classe) {
        return getStreamFilhas(classe).findFirst().orElse(null);
    }

    private <I extends ProcessInstance, K extends ProcessDefinition<I>> Stream<I> getStreamFilhas(Class<K> classe) {
        K def = MBPM.getDefinicao(classe);
        IEntityProcess dadosDefinicaoProcesso = def.getEntity();
        return getInternalEntity().getDemandasFilhas().stream().filter(d -> d.getDefinicao().equals(dadosDefinicaoProcesso))
                .map(def::convertToProcessInstance);
    }

    public TaskInstance getTarefaPai() {
        IEntityTaskInstance dbTaskInstance = getInternalEntity().getTarefaPai();
        return dbTaskInstance == null ? null : MBPM.getTaskInstance(dbTaskInstance);
    }

    public TaskInstance iniciar() {
        return iniciar(null);
    }

    public TaskInstance iniciar(VarInstanceMap<?> paramIn) {
        getDescricaoBD(); // Força a geração da descricação
        TaskInstance tarefaAtual = getTarefaPai();
        if (tarefaAtual == null && getInternalEntity().getDemandaPai() != null) {
            tarefaAtual = MBPM.getMbpmBean().getProcessInstance(getInternalEntity().getDemandaPai()).getTarefaAtual();
        }
        if (tarefaAtual != null) {
            tarefaAtual.log("Demanda Criada", getDescricaoExtendida(), null, MBPM.getUserIfAvailable(), null, getInternalEntity());
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
            String transitionName = null;
            if (transicaoOrigem != null) {
                transitionName = transicaoOrigem.getName();
            }
            getPersistenceService().endTask(tarefaOrigem.getEntityTaskInstance(), transitionName, MBPM.getUserIfAvailable());
        }
        IEntityTaskDefinition situacaoNova = getDefinicao().getEntityTask(task);

        IEntityTaskInstance tarefa = getPersistenceService().addTask(getEntity(), situacaoNova);

        TaskInstance tarefaNova = getTarefa(tarefa);
        estadoAtual = task;

        MBPM.getMbpmBean().notifyStateUpdate(this);
        return tarefaNova;
    }

    public MTask<?> getEstado() {
        if (estadoAtual == null) {
            estadoAtual = getDefinicao().getFlowMap().getTaskWithAbbreviation(getInternalEntity().getSituacao().getSigla());
        }
        return estadoAtual;
    }

    public boolean isFim() {
        return getEntity().getSituacao().isFim();
    }

    /**
     * @param nomeEstado
     * @return
     */
    public boolean isNomeEstado(String nomeEstado) {
        final MTask<?> task = getDefinicao().getFlowMap().getTaskWithName(nomeEstado);
        if (task == null) {
            throw getDefinicao().getFlowMap().createError("Não existe task com nome '" + nomeEstado + "'");
        }
        return getEstado().equals(task);
    }

    /**
     * @param sigla
     * @return
     */
    public boolean isSiglaEstado(String sigla) {
        final MTask<?> task = getDefinicao().getFlowMap().getTaskWithAbbreviation(sigla);
        if (task == null) {
            throw getDefinicao().getFlowMap().createError("Não existe task com sigla '" + sigla + "'");
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
        return getDefinicao().getName();
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

    public final Lnk getDefaultHref() {
        return MBPM.getDefaultHrefFor(this);
    }

    public Set<Integer> getRhComDireitoTarefa(String nomeTarefa) {
        return getDefinicao().getFlowMap().getPeopleTaskWithAbbreviation(nomeTarefa).getAccessStrategy().getFirstLevelUsersCodWithAccess(this);
    }

    public final String getTitulo() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getNomeProcesso());
        String d;
        if (getDefinicao().getFlowMap().hasMultiplePeopleTasks()) {
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

    public final boolean canExecuteTask(MUser user) {
        if (getEstado() == null) {
            return false;
        }
        switch (getEstado().getTaskType()) {
            case People:
            case Wait:
                return (isAlocado(user.getCod()))
                        || (getAccessStrategy() != null && getAccessStrategy().canExecute(this, user));
            default:
                return false;
        }
    }

    public boolean canVisualize(MUser user) {
        switch (getInternalEntity().getSituacao().getTipoTarefa()) {
            case People:
            case Wait:
                if (temAlocado() && isAlocado(user.getCod())) {
                    return true;
                }
            default:
        }
        return getAccessStrategy() != null && getAccessStrategy().canVisualize(this, user);
    }

    public Set<Integer> getFirstLevelUsersCodWithAccess() {
        return getAccessStrategy().getFirstLevelUsersCodWithAccess(this);
    }

    public List<MUser> listAllocableUsers() {
        return getAccessStrategy().listAllocableUsers(this);
    }

    /**
     * @deprecated Deveria ter uma exceção de Runtime do próprio Singular
     */
    @Deprecated
    //TODO refatorar
    protected final RuntimeException criarErro(String msg) {
        return MBPMUtil.generateError(this, msg);
    }

    @SuppressWarnings("rawtypes")
    private TaskAccessStrategy getAccessStrategy() {
        return getEstado().getAccessStrategy();
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final void refresh() {
        getPersistenceService().refreshModel(getInternalEntity());
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final <K extends IEntityProcessInstance> K getDemandaSemRefresh() {
        if (entity.getCod() == null) {
            return saveEntity();
        }
        return (K) entity;

    }

    public final IEntityProcessInstance getEntity() {
        if (entity.getCod() == null) {
            return saveEntity();
        }
        entity = getPersistenceService().retrieveProcessInstanceByCod(entity.getCod());
        return entity;
    }

    public MUser getPessoaInteressada() {
        return getInternalEntity().getPessoaCriadora();
    }

    public MUser getUserWithRole(String roleAbbreviation) {
        final IEntityRole entityRole = getEntity().getRoleUserByAbbreviation(roleAbbreviation);
        if (entityRole != null) {
            return entityRole.getPessoa();
        }
        return null;
    }

    public List<? extends IEntityRole> getUserRoles() {
        return getEntity().getPapeis();
    }

    public IEntityRole getRoleUserByAbbreviation(String roleAbbreviation) {
        return getEntity().getRoleUserByAbbreviation(roleAbbreviation);
    }

    public boolean hasUserRoles() {
        return !getEntity().getPapeis().isEmpty();
    }

    public void setPessoaCriadora(MUser pessoaCriadora) {
        getInternalEntity().setPessoaCriadora(pessoaCriadora);
    }

    public MUser getPessoaCriadora() {
        return getInternalEntity().getPessoaCriadora();
    }

    protected void setDescricao(String descricao) {
        getInternalEntity().setDescricao(StringUtils.left(descricao, 250));
    }

    public final <K extends IEntityProcessInstance> K saveEntity() {
        entity = getPersistenceService().saveProcessInstance(entity);
        return (K) entity;
    }

    public final void forcarEstado(MTask<?> task) {
        final TaskInstance tarefaOrigem = getTarefaAtual();
        List<MUser> pessoasAnteriores = getResponsaveisDiretos();
        final Date agora = new Date();
        TaskInstance tarefaNova = updateEstado(tarefaOrigem, null, task, agora);
        if (tarefaOrigem != null) {
            tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getNome() + "' para '" + task.getName() + "'",
                    null, MBPM.getUserIfAvailable(), agora).sendEmail(pessoasAnteriores);
        }

        ExecucaoMTask execucaoMTask = new ExecucaoMTask(this, tarefaNova, null);
        task.notifyTaskStart(getTarefaMaisRecenteComNome(task.getName()), execucaoMTask);
    }

    public Date getDataAlvoFim() {
        final IEntityTaskInstance tarefa = getInternalEntity().getTarefaAtiva();
        if (tarefa != null) {
            return tarefa.getDataAlvoFim();
        }
        return null;
    }

    public Date getDataInicio() {
        return getInternalEntity().getDataInicio();
    }

    public Date getDataFim() {
        return getInternalEntity().getDataFim();
    }

    public Integer getEntityCod() {
        return entity.getCod();
    }

    public String getId() {
        return entity.getCod().toString();
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
        String descricao = getInternalEntity().getDescricao();
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
        if (!StringUtils.isBlank(descricao) && !descricao.equalsIgnoreCase(getInternalEntity().getDescricao())) {
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
        return getEntity().getCod().toString();
    }

    public List<MUser> getResponsaveisDiretos() {
        TaskInstance tarefa = getTarefaAtual();
        if (tarefa != null) {
            return tarefa.getDirectlyResponsible();
        }
        return Collections.emptyList();
    }

    private void addUserRole(MProcessRole mProcessRole, MUser user) {
        if (getUserWithRole(mProcessRole.getAbbreviation()) == null) {
            getPersistenceService().setInstanceUserRole(getEntity(), getDefinicao().getEntity().getPapel(mProcessRole.getAbbreviation()), user);
        }
    }

    public final void addOrReplaceUserRole(final String roleAbbreviation, MUser newUser) {
        MProcessRole mProcessRole = getDefinicao().getFlowMap().getRoleWithAbbreviation(roleAbbreviation);
        MUser previousUser = getUserWithRole(mProcessRole.getAbbreviation());

        if (previousUser == null) {
            if (newUser != null) {
                addUserRole(mProcessRole, newUser);
                getDefinicao().getFlowMap().notifyRoleChange(this, mProcessRole, null, newUser);

                final TaskInstance latestTask = getLatestTask();
                if (latestTask != null) {
                    latestTask.log("Papel definido", String.format("%s: %s", mProcessRole.getName(), newUser.getNomeGuerra()));
                }
            }
        } else if (newUser == null || !previousUser.equals(newUser)) {
            getPersistenceService().removeInstanceUserRole(getEntity(), getEntity().getRoleUserByAbbreviation(mProcessRole.getAbbreviation()));
            if (newUser != null) {
                addUserRole(mProcessRole, newUser);
            }

            getDefinicao().getFlowMap().notifyRoleChange(this, mProcessRole, previousUser, newUser);
            final TaskInstance latestTask = getLatestTask();
            if (latestTask != null) {
                if (newUser != null) {
                    latestTask.log("Papel alterado", String.format("%s: %s", mProcessRole.getName(), newUser.getNomeGuerra()));
                } else {
                    latestTask.log("Papel removido", mProcessRole.getName());
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
        if (variables == null) {
            variables = new VarInstanceTableProcess(this);
        }
        return variables;
    }

    protected void validarPreInicio() {
        if (variables == null && !getDefinicao().getVariables().hasRequired()) {
            return;
        }

        ValidationResult result = getVariaveis().validar();
        if (result.hasErros()) {
            throw criarErro("Erro ao iniciar processo '" + getNomeProcesso() + "': " + result);
        }
    }

    public boolean temAlocado() {
        return getEntity().getTarefas().stream().anyMatch(tarefa -> isTarefaAtiva(tarefa) && tarefa.getPessoaAlocada() != null);
    }

    public boolean isAlocado(Integer codPessoa) {
        return getEntity()
                .getTarefas()
                .stream()
                .anyMatch(
                        tarefa -> isTarefaAtiva(tarefa) && tarefa.getPessoaAlocada() != null
                                && tarefa.getPessoaAlocada().getCod().equals(codPessoa));
    }

    public List<TaskInstance> getTarefas() {
        IEntityProcessInstance demanda = getEntity();
        return demanda.getTarefas().stream().map(this::getTarefa).collect(Collectors.toList());
    }

    private TaskInstance findFirstTaskInstance(boolean searchFromEnd, Predicate<IEntityTaskInstance> condicao) {
        List<? extends IEntityTaskInstance> lista = getEntity().getTarefas();
        if (searchFromEnd) {
            lista = Lists.reverse(lista);
        }

        return getTarefa(lista.stream().filter(condicao).findFirst().orElse(null));
    }

    private static boolean isTarefaAtiva(IEntityTaskInstance tarefa) {
        return tarefa.getDataFim() == null;
    }

    public TaskInstance getUltimaTarefa() {
        return findFirstTaskInstance(true, t -> true);
    }

    public TaskInstance getTarefaAtual() {
        return findFirstTaskInstance(true, ProcessInstance::isTarefaAtiva);
    }

    public TaskInstance getTarefaMaisRecenteComNome(final String nomeTipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getSituacao().getNome().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getLatestTask() {
        return findFirstTaskInstance(true, tarefa -> true);
    }

    public TaskInstance getUltimaTarefaConcluidaComNome(final String nomeTipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getNome().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getUltimaTarefaConcluidaTipo(final MTask<?> tipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getSigla().equalsIgnoreCase(tipo.getAbbreviation()));
    }

    public TaskInstance getUltimaTarefaConcluida(final TaskType tipoTarefa) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getDataFim() != null && tarefa.getSituacao().getTipoTarefa().equals(tipoTarefa));
    }

    protected IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return getDefinicao().getPersistenceService();
    }

    /**
     * @deprecated Deveria ter uma exceção de Runtime do próprio Singular
     */
    @Deprecated
    //TODO refatorar
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
