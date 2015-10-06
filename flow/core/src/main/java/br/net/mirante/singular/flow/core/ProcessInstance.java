package br.net.mirante.singular.flow.core;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.commons.base.SingularException;
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
import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.view.Lnk;

/**
 * <p>Esta é a classe responsável por manter os dados de instância de um determinado processo.</p>
 *
 * @author Mirante Tecnologia
 */
@SuppressWarnings({"serial", "unchecked"})
public class ProcessInstance {

    private RefProcessDefinition processDefinitionRef;

    private transient MTask<?> estadoAtual;

    private transient ExecucaoMTask executionContext;

    /**
     * @deprecated não proliferar o uso desse campo, utilzar getInternalEntity no lugar
     */
    @Deprecated
    private transient IEntityProcessInstance entity;

    private transient VarInstanceMap<?> variables;

    private transient VariableWrapper variableWrapper;

    final void setProcessDefinition(ProcessDefinition<?> processDefinition) {
        if (processDefinitionRef != null) {
            throw new SingularException("Erro Interno");
        }
        processDefinitionRef = RefProcessDefinition.of(processDefinition);
    }

    /**
     * <p>Retorna a definição de processo desta instância.</p>
     *
     * @param <K> o tipo da definição de processo.
     * @return a definição de processo desta instância.
     */
    public <K extends ProcessDefinition<?>> K getProcessDefinition() {
        if (processDefinitionRef == null) {
            throw new SingularException("A instância não foi inicializada corretamente, pois não tem uma referência a ProcessDefinition ");
        }
        return (K) processDefinitionRef.get();
    }

    /**
     * <p>Inicia esta instância de processo.</p>
     *
     * @return A tarefa atual da instância depois da inicialização.
     */
    public TaskInstance start() {
        return start(getVariaveis());
    }

    /**
     * @deprecated Esse método deve ser renomeado pois possui um comportamente
     *             implicito não evidente em comparação à outra versão
     *             sobrecarregada do mesmo: "getPersistedDescription"
     */
    @Deprecated
    public TaskInstance start(VarInstanceMap<?> varInstanceMap) {
        getPersistedDescription(); // Força a geração da descricação
        return EngineProcessamentoMBPM.start(this, varInstanceMap);
    }

    /**
     * <p>Executa a próxima transição desta instância de processo.</p>
     */
    public void executeTransition() {
        EngineProcessamentoMBPM.executeTransition(this, null, null);
    }

    /**
     * <p>Executa a transição especificada desta instância de processo.</p>
     *
     * @param transitionName a transição especificada.
     */
    public void executeTransition(String transitionName) {
        EngineProcessamentoMBPM.executeTransition(this, transitionName, null);
    }

    /**
     * <p>Executa a transição especificada desta instância de processo passando
     * as variáveis fornecidas.</p>
     *
     * @param transitionName a transição especificada.
     * @param param as variáveis fornecidas.
     */
    public void executeTransition(String transitionName, VarInstanceMap<?> param) {
        EngineProcessamentoMBPM.executeTransition(this, transitionName, param);
    }

    /**
     * <p>Realiza a montagem necessária para execução da transição especificada a partir
     * da tarefa atual desta instância.</p>
     *
     * @param transitionName a transição especificada.
     * @return a montagem resultante.
     */
    public TransitionCall prepareTransition(String transitionName) {
        return getCurrentTask().prepareTransition(transitionName);
    }

    final IEntityProcessInstance getInternalEntity() {
        if (entity == null) {
            throw new SingularException(
                    getClass().getName() + " is not binded to a new and neither to a existing database intance process entity.");
        }
        return entity;
    }

    private TaskInstance getCurrentTaskOrException() {
        TaskInstance current = getCurrentTask();
        if (current == null) {
            throw new SingularException(createErrorMsg("Não há um task atual para essa instancia"));
        }
        return current;
    }

    final void setInternalEntity(IEntityProcessInstance entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;
    }

    /**
     * <p>Configura a instância "pai" desta instância de processo.</p>
     *
     * @param pai a instância "pai".
     */
    public void setParent(ProcessInstance pai) {
        getPersistenceService().setProcessInstanceParent(getInternalEntity(), pai.getInternalEntity());
    }

    /**
     * <p>Retorna a tarefa "pai" desta instância de processo.</p>
     *
     * @return a tarefa "pai".
     */
    public TaskInstance getParentTask() {
        IEntityTaskInstance dbTaskInstance = getInternalEntity().getParentTask();
        return dbTaskInstance == null ? null : Flow.getTaskInstance(dbTaskInstance);
    }

    /**
     * <p>Retorna o tarefa corrente desta instância de processo.</p>
     *
     * @return a tarefa corrente.
     */
    public MTask<?> getEstado() {
        if (estadoAtual == null) {
            TaskInstance current = getCurrentTask();
            if (current != null) {
                estadoAtual = getProcessDefinition().getFlowMap().getTaskBybbreviation(current.getAbbreviation());
            } else if (isFinished()) {
                throw new SingularException(createErrorMsg(
                        "incossitencia: o estado final está null, mas deveria ter um estado do tipo final por estar finalizado"));
            } else {
                throw new SingularException(createErrorMsg("getEstado() não pode ser invocado para essa instância"));
            }
        }
        return estadoAtual;
    }

    /**
     * <p>Verifica se esta instância está encerrada.</p>
     *
     * @return {@code true} caso esta instância está encerrada; {@code false} caso contrário.
     */
    public boolean isFinished() {
        return getEndDate() != null;
    }

    /**
     * <p>Retornar o nome da definição de processo desta instância.</p>
     *
     * @return o nome da definição de processo.
     */
    public String getProcessName() {
        return getProcessDefinition().getName();
    }

    /**
     * <p>Retorna o nome da tarefa atual desta instância de processo.</p>
     *
     * @return o nome da tarefa atual; ou {@code null} caso não haja uma tarefa atual.
     */
    public String getCurrentTaskName() {
        if (getEstado() != null) {
            return getEstado().getName();
        }
        TaskInstance tarefaAtual = getCurrentTask();
        if (tarefaAtual != null) {
            // Uma situação legada, que não existe mais no fluxo mapeado
            return tarefaAtual.getName();
        }
        return null;
    }

    /**
     * <p>Retorna o <i>link resolver</i> padrão desta instância de processo.</p>
     *
     * @return o <i>link resolver</i> padrão.
     */
    public final Lnk getDefaultHref() {
        return Flow.getDefaultHrefFor(this);
    }

    /**
     * <p>Retorna os códigos de usuários com direito de execução da tarefa humana definida
     * para o processo correspondente a esta instância.</p>
     *
     * @param nomeTarefa o nome da tarefa humana a ser inspecionada.
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess(String nomeTarefa) {
        return getProcessDefinition().getFlowMap().getPeopleTaskByAbbreviationOrException(nomeTarefa).getAccessStrategy()
                .getFirstLevelUsersCodWithAccess(this);
    }

    /**
     * <p>Verifica de o usuário especificado pode executar a tarefa corrente desta instância de processo.</p>
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa executar a tarefa corrente; {@code false} caso contrário.
     */
    public final boolean canExecuteTask(MUser user) {
        if (getEstado() == null) {
            return false;
        }
        IEntityTaskType tt = getEstado().getTaskType();
        if (tt.isPeople() || tt.isWait()) {
            return (isAllocated(user.getCod()))
                    || (getAccessStrategy() != null && getAccessStrategy().canExecute(this, user));

        }
        return false;
    }

    /**
     * <p>Verifica de o usuário especificado pode visualizar a tarefa corrente desta instância de processo.</p>
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa visualizar a tarefa corrente; {@code false} caso contrário.
     */
    public boolean canVisualize(MUser user) {
        MTask<?> tt = getCurrentTaskOrException().getFlowTask();
        if (tt.isPeople() || tt.isWait()) {
            if (hasAllocatedUser() && isAllocated(user.getCod())) {
                return true;
            }

        }
        return getAccessStrategy() != null && getAccessStrategy().canVisualize(this, user);
    }

    /**
     * <p>Retorna os códigos de usuários com direito de execução da tarefa corrente desta
     * instância de processo.</p>
     *
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess() {
        return getAccessStrategy().getFirstLevelUsersCodWithAccess(this);
    }

    /**
     * <p>Retorna os usuários com direito de execução da tarefa corrente desta instância de processo.</p>
     *
     * @return os usuários com direitos de execução.
     */
    public List<MUser> listAllocableUsers() {
        return getAccessStrategy().listAllocableUsers(this);
    }


    /**
     * <p>Formata uma mensagem de erro.</p>
     *
     * <p>A formatação da mensagem segue o seguinte padrão:</p>
     *
     * <pre>
     *     getClass().getName() + " - " + getFullId() + " : " + message
     * </pre>
     *
     * @param message a mensagem a ser formatada.
     * @return a mensagem formatada.
     * @see #getFullId()
     */
    public final String createErrorMsg(String message) {
        return getClass().getName() + " - " + getFullId() + " : " + message;
    }

    @SuppressWarnings("rawtypes")
    private TaskAccessStrategy getAccessStrategy() {
        return getEstado().getAccessStrategy();
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final void refreshEntity() {
        getPersistenceService().refreshModel(getInternalEntity());
    }

    /**
     * <p>Recupera a entidade persistente correspondente a esta instância de processo.</p>
     *
     * @return a entidade persistente.
     */
    public final IEntityProcessInstance getEntity() {
        if (getInternalEntity().getCod() == null) {
            return saveEntity();
        }
        setInternalEntity(getPersistenceService().retrieveProcessInstanceByCod(getInternalEntity().getCod()));
        return getInternalEntity();
    }

    /**
     * <p>Retorna o usuário desta instância de processo atribuído ao papel especificado.</p>
     *
     * @param roleAbbreviation a sigla do papel especificado.
     * @return o usuário atribuído ao papel.
     */
    public final MUser getUserWithRole(String roleAbbreviation) {
        final IEntityRole entityRole = getEntity().getRoleUserByAbbreviation(roleAbbreviation);
        if (entityRole != null) {
            return entityRole.getUser();
        }
        return null;
    }

    public final List<? extends IEntityRole> getUserRoles() {
        return getEntity().getRoles();
    }

    public final IEntityRole getRoleUserByAbbreviation(String roleAbbreviation) {
        return getEntity().getRoleUserByAbbreviation(roleAbbreviation);
    }

    public final boolean hasUserRoles() {
        return !getEntity().getRoles().isEmpty();
    }

    public final MUser getUserCreator() {
        return getInternalEntity().getUserCreator();
    }

    public final void setDescription(String descricao) {
        getInternalEntity().setDescription(StringUtils.left(descricao, 250));
    }

    public final <K extends IEntityProcessInstance> K saveEntity() {
        setInternalEntity(getPersistenceService().saveProcessInstance(getInternalEntity()));
        return (K) getInternalEntity();
    }

    public final void forceStateUpdate(MTask<?> task) {
        final TaskInstance tarefaOrigem = getCurrentTask();
        List<MUser> pessoasAnteriores = getResponsaveisDiretos();
        final Date agora = new Date();
        TaskInstance tarefaNova = updateState(tarefaOrigem, null, task, agora);
        if (tarefaOrigem != null) {
            tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getName() + "' para '" + task.getName() + "'",
                    null, Flow.getUserIfAvailable(), agora).sendEmail(pessoasAnteriores);
        }

        ExecucaoMTask execucaoMTask = new ExecucaoMTask(this, tarefaNova, null);
        task.notifyTaskStart(getLatestTask(task), execucaoMTask);
    }

    protected final TaskInstance updateState(TaskInstance tarefaOrigem, MTransition transicaoOrigem, MTask<?> task, Date agora) {
        synchronized (this) {
            if (tarefaOrigem != null) {
                String transitionName = null;
                if (transicaoOrigem != null) {
                    transitionName = transicaoOrigem.getName();
                }
                getPersistenceService().completeTask(tarefaOrigem.getEntityTaskInstance(), transitionName, Flow.getUserIfAvailable());
            }
            IEntityTaskVersion situacaoNova = getProcessDefinition().getEntityTaskVersion(task);

            IEntityTaskInstance tarefa = getPersistenceService().addTask(getEntity(), situacaoNova);

            TaskInstance tarefaNova = getTaskInstance(tarefa);
            estadoAtual = task;

            Flow.getMbpmBean().notifyStateUpdate(this);
            return tarefaNova;
        }
    }

    public final Date getBeginDate() {
        return getInternalEntity().getBeginDate();
    }

    public final Date getEndDate() {
        return getInternalEntity().getEndDate();
    }

    public final Integer getEntityCod() {
        return getInternalEntity().getCod();
    }

    public final String getId() {
        return getInternalEntity().getCod().toString();
    }

    public final String getFullId() {
        return Flow.generateID(this);
    }

    private TaskInstance getTaskInstance(final IEntityTaskInstance tarefa) {
        return tarefa != null ? new TaskInstance(this, tarefa) : null;
    }

    /**
     * O mesmo que getDescricaoCompleta.
     */
    public String getDescription() {
        return getCompleteDescription();
    }

    /**
     * Nome do processo seguido da descrição completa.
     */
    public final String getExtendedDescription() {
        String descricao = getDescription();
        if (descricao == null) {
            return getProcessName();
        }
        return getProcessName() + " - " + descricao;
    }

    protected final String getPersistedDescription() {
        String descricao = getInternalEntity().getDescription();
        if (descricao == null) {
            descricao = generateInitialDescription();
            if (!StringUtils.isBlank(descricao)) {
                setDescription(descricao);
            }
        }
        return descricao;
    }

    /**
     * Cria a descrição que vai gravada no banco de dados. Deve ser sobreescrito
     * para ter efeito
     */
    protected String generateInitialDescription() {
        return null;
    }

    /**
     * Sobrescreve a descrição da demanda a partir do método
     * {@link #generateInitialDescription()}
     *
     * @return true caso tenha sido alterada a descrição
     */
    public final boolean regenerateInitialDescription() {
        String descricao = generateInitialDescription();
        if (!StringUtils.isBlank(descricao) && !descricao.equalsIgnoreCase(getInternalEntity().getDescription())) {
            setDescription(descricao);
            return true;
        }
        return false;
    }

    /**
     * Cria versão extendida da descrição em relação ao campo descrição no BD.
     * Geralmente são adicionadas informações que não precisam ter cache feito
     * em banco de dados.
     */
    protected String getCompleteDescription() {
        return getPersistedDescription();
    }

    public List<MUser> getResponsaveisDiretos() {
        TaskInstance tarefa = getCurrentTask();
        if (tarefa != null) {
            return tarefa.getDirectlyResponsibles();
        }
        return Collections.emptyList();
    }

    private void addUserRole(MProcessRole mProcessRole, MUser user) {
        if (getUserWithRole(mProcessRole.getAbbreviation()) == null) {
            getPersistenceService().setInstanceUserRole(getEntity(), getProcessDefinition().getEntity().getRole(mProcessRole.getAbbreviation()), user);
        }
    }

    public final void addOrReplaceUserRole(final String roleAbbreviation, MUser newUser) {
        MProcessRole mProcessRole = getProcessDefinition().getFlowMap().getRoleWithAbbreviation(roleAbbreviation);
        if (mProcessRole == null) {
            throw new SingularFlowException("Não foi possível encontrar a role: " + roleAbbreviation);
        }
        MUser previousUser = getUserWithRole(mProcessRole.getAbbreviation());

        if (previousUser == null) {
            if (newUser != null) {
                addUserRole(mProcessRole, newUser);
                getProcessDefinition().getFlowMap().notifyRoleChange(this, mProcessRole, null, newUser);

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

            getProcessDefinition().getFlowMap().notifyRoleChange(this, mProcessRole, previousUser, newUser);
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
        getProcessDefinition().verifyVariableWrapperClass(newVariableSet.getClass());
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

    protected void validadeStart() {
        if (variables == null && !getProcessDefinition().getVariables().hasRequired()) {
            return;
        }

        ValidationResult result = getVariaveis().validar();
        if (result.hasErros()) {
            throw new SingularFlowException(createErrorMsg("Erro ao iniciar processo '" + getProcessName() + "': " + result));
        }
    }

    public boolean hasAllocatedUser() {
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && tarefa.getAllocatedUser() != null);
    }

    public boolean isAllocated(Integer codPessoa) {
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && tarefa.getAllocatedUser() != null
                && tarefa.getAllocatedUser().getCod().equals(codPessoa));
    }

    /**
     * Retorna a lista de todas as tasks da mais antiga para a mais novo.
     *
     * @return Nunca null
     */
    public List<TaskInstance> getTasks() {
        IEntityProcessInstance demanda = getEntity();
        return demanda.getTasks().stream().map(this::getTaskInstance).collect(Collectors.toList());
    }

    /**
     * Retorna a mais nova task que atende a condicao informada
     *
     * @return pode ser null
     */
    public TaskInstance getLatestTask(Predicate<TaskInstance> condicao) {
        List<? extends IEntityTaskInstance> lista = getEntity().getTasks();
        for (int i = lista.size() - 1; i != -1; i--) {
            TaskInstance task = getTaskInstance(lista.get(i));
            if (condicao.test(task)) {
                return task;
            }
        }
        return null;
    }

    public TaskInstance getCurrentTask() {
        return getLatestTask(t -> t.isActive());
    }

    /**
     * Retorna a mais nova task encerrada ou ativa.
     */
    public TaskInstance getLatestTask() {
        return getLatestTask(t -> true);
    }

    /**
     * Encontra a mais nova task encerrada ou ativa com a mesma sigla informada.
     *
     * @return Pode ser null
     */
    private TaskInstance getLatestTask(String abbreviation) {
        return getLatestTask(t -> t.getAbbreviation().equalsIgnoreCase(abbreviation));
    }

    /**
     * Encontra a mais nova task encerrada ou ativa com a sigla da referencia.
     *
     * @return Pode ser null
     */
    public TaskInstance getLatestTask(ITaskDefinition taskRef) {
        return getLatestTask(taskRef.getKey());
    }

    /**
     * Encontra a mais nova task encerrada ou ativa do tipo informado.
     *
     * @return Pode ser null
     */
    public TaskInstance getLatestTask(MTask<?> tipo) {
        return getLatestTask(tipo.getAbbreviation());
    }

    /**
     * Encontra a mais nova task encerrada e com a mesma sigla informada.
     *
     * @return Pode ser null
     */
    private TaskInstance getFinishedTask(String abbreviation) {
        return getLatestTask(t -> t.isFinished() && t.getAbbreviation().equalsIgnoreCase(abbreviation));
    }

    /**
     * Encontra a mais nova task encerrada e com a mesma sigla da referência.
     *
     * @return Pode ser null
     */
    public TaskInstance getFinishedTask(ITaskDefinition taskRef) {
        return getFinishedTask(taskRef.getKey());
    }

    /**
     * Encontra a mais nova task encerrada e com a mesma sigla do tipo.
     *
     * @return Pode ser null
     */
    public TaskInstance getFinishedTask(MTask<?> tipo) {
        return getFinishedTask(tipo.getAbbreviation());
    }

    protected IPersistenceService<IEntityCategory, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return getProcessDefinition().getPersistenceService();
    }

    final void setExecutionContext(ExecucaoMTask execucaoTask) {
        if (this.executionContext != null && execucaoTask != null) {
            throw new SingularFlowException(createErrorMsg("A instancia já está com um tarefa em processo de execução"));
        }
        this.executionContext = execucaoTask;
    }

    protected final <T extends VariableWrapper> T getVariablesWrapper(Class<T> variableWrapperClass) {
        if (variableWrapper == null) {
            if (variableWrapperClass != getProcessDefinition().getVariableWrapperClass()) {
                throw new SingularFlowException("A classe do parâmetro (" + variableWrapperClass.getName() + ") é diferente da definida em "
                        + getDescription().getClass().getName() + ". A definição do processo informou o wrapper como sendo "
                        + getProcessDefinition().getVariableWrapperClass());
            }
            try {
                variableWrapper = variableWrapperClass.getConstructor().newInstance();
                variableWrapper.setVariables(getVariaveis());
            } catch (Exception e) {
                throw new SingularFlowException("Erro instanciando variableWrapper: " + e.getMessage(), e);
            }
        }
        return variableWrapperClass.cast(variableWrapper);
    }
}
