package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.ValidationResult;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.view.Lnk;

@SuppressWarnings({"serial", "unchecked"})
public abstract class ProcessInstance {

    private final RefProcessDefinition processDefinitionRef;

    private transient MTask<?> estadoAtual;

    private transient ExecucaoMTask executionContext;

    private transient IEntityProcessInstance entity;

    private transient VarInstanceMap<?> variables;

    private transient VariableWrapper variableWrapper;

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> definitionClass) {
        processDefinitionRef = RefProcessDefinition.loadByClass(definitionClass);
        entity = getProcessDefinition().createProcessInstance();
    }

    protected ProcessInstance(Class<? extends ProcessDefinition<?>> definitionClass, IEntityProcessInstance entityProcessInstance) {
        processDefinitionRef = RefProcessDefinition.loadByClass(definitionClass);
        entity = entityProcessInstance;
    }

    public <K extends ProcessDefinition<?>> K getProcessDefinition() {
        return (K) processDefinitionRef.get();
    }

    public TaskInstance start() {
        return start(null);
    }

    public TaskInstance start(VarInstanceMap<?> paramIn) {
        getPersistedDescription(); // Força a geração da descricação
        return EngineProcessamentoMBPM.start(this, paramIn);
    }

    public void executeTransition() {
        EngineProcessamentoMBPM.executeTransition(this, null, null);
    }

    public void executeTransition(String destino) {
        EngineProcessamentoMBPM.executeTransition(this, destino, null);
    }

    public void executeTransition(String destino, VarInstanceMap<?> param) {
        EngineProcessamentoMBPM.executeTransition(this, destino, param);
    }

    public TransitionCall prepareTransition(String transitionName) {
        return getCurrentTask().prepareTransition(transitionName);
    }
    
    IEntityProcessInstance getInternalEntity() {
        return entity;
    }
    
    protected void setParent(ProcessInstance pai) {
        getPersistenceService().setProcessInstanceParent(getInternalEntity(), pai.getInternalEntity());
    }
    
    public TaskInstance getParentTask() {
        IEntityTaskInstance dbTaskInstance = getInternalEntity().getParentTask();
        return dbTaskInstance == null ? null : MBPM.getTaskInstance(dbTaskInstance);
    }

    public MTask<?> getEstado() {
        if (estadoAtual == null) {
            estadoAtual = getProcessDefinition().getFlowMap().getTaskWithAbbreviation(getInternalEntity().getCurrentTask().getTask().getAbbreviation());
        }
        return estadoAtual;
    }

    public boolean isEnd() {
        return getEntity().getCurrentTask().getTask().isEnd();
    }

    public String getProcessName() {
        return getProcessDefinition().getName();
    }

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

    public final Lnk getDefaultHref() {
        return MBPM.getDefaultHrefFor(this);
    }

    public Set<Serializable> getFirstLevelUsersCodWithAccess(String nomeTarefa) {
        return getProcessDefinition().getFlowMap().getPeopleTaskWithAbbreviationOrException(nomeTarefa).getAccessStrategy()
                .getFirstLevelUsersCodWithAccess(this);
    }

    public final boolean canExecuteTask(MUser user) {
        if (getEstado() == null) {
            return false;
        }
        switch (getEstado().getTaskType()) {
            case People:
            case Wait:
                return (isAllocated(user.getCod()))
                        || (getAccessStrategy() != null && getAccessStrategy().canExecute(this, user));
            default:
                return false;
        }
    }

    public boolean canVisualize(MUser user) {
        switch (getInternalEntity().getCurrentTask().getTask().getType()) {
            case People:
            case Wait:
                if (hasAllocatedUser() && isAllocated(user.getCod())) {
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

    public final IEntityProcessInstance getEntity() {
        if (entity.getCod() == null) {
            return saveEntity();
        }
        entity = getPersistenceService().retrieveProcessInstanceByCod(entity.getCod());
        return entity;
    }

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

    protected final void setDescription(String descricao) {
        getInternalEntity().setDescription(StringUtils.left(descricao, 250));
    }

    public final <K extends IEntityProcessInstance> K saveEntity() {
        entity = getPersistenceService().saveProcessInstance(entity);
        return (K) entity;
    }

    public final void forceStateUpdate(MTask<?> task) {
        final TaskInstance tarefaOrigem = getCurrentTask();
        List<MUser> pessoasAnteriores = getResponsaveisDiretos();
        final Date agora = new Date();
        TaskInstance tarefaNova = updateState(tarefaOrigem, null, task, agora);
        if (tarefaOrigem != null) {
            tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getName() + "' para '" + task.getName() + "'",
                    null, MBPM.getUserIfAvailable(), agora).sendEmail(pessoasAnteriores);
        }

        ExecucaoMTask execucaoMTask = new ExecucaoMTask(this, tarefaNova, null);
        task.notifyTaskStart(getTarefaMaisRecenteComNome(task.getName()), execucaoMTask);
    }
    
    protected final TaskInstance updateState(TaskInstance tarefaOrigem, MTransition transicaoOrigem, MTask<?> task, Date agora) {
        synchronized (this) {
            if (tarefaOrigem != null) {
                String transitionName = null;
                if (transicaoOrigem != null) {
                    transitionName = transicaoOrigem.getName();
                }
                getPersistenceService().completeTask(tarefaOrigem.getEntityTaskInstance(), transitionName, MBPM.getUserIfAvailable());
            }
            IEntityTask situacaoNova = getProcessDefinition().getEntityTask(task);
            
            IEntityTaskInstance tarefa = getPersistenceService().addTask(getEntity(), situacaoNova);
            
            TaskInstance tarefaNova = getTaskInstance(tarefa);
            estadoAtual = task;
            
            MBPM.getMbpmBean().notifyStateUpdate(this);
            return tarefaNova;
        }
    }

    public final Date getBeginDate() {
        return getInternalEntity().getBeginDate();
    }

    public final Date getEndDate() {
        return getInternalEntity().getEndDate();
    }

    public final Serializable getEntityCod() {
        return entity.getCod();
    }

    public final String getId() {
        return entity.getCod().toString();
    }

    public final String getFullId() {
        return MBPM.generateID(this);
    }

    public TaskInstance getTaskInstance(final IEntityTaskInstance tarefa) {
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
        return getEntity().getTasks().stream().anyMatch(tarefa -> isActiveTask(tarefa) && tarefa.getAllocatedUser() != null);
    }

    public boolean isAllocated(Serializable codPessoa) {
        return getEntity()
                .getTasks()
                .stream()
                .anyMatch(
                        tarefa -> isActiveTask(tarefa) && tarefa.getAllocatedUser() != null
                                && tarefa.getAllocatedUser().getCod().equals(codPessoa));
    }

    public List<TaskInstance> getTasks() {
        IEntityProcessInstance demanda = getEntity();
        return demanda.getTasks().stream().map(this::getTaskInstance).collect(Collectors.toList());
    }

    private TaskInstance findFirstTaskInstance(boolean searchFromEnd, Predicate<IEntityTaskInstance> condicao) {
        List<? extends IEntityTaskInstance> lista = getEntity().getTasks();
        if (searchFromEnd) {
            lista = Lists.reverse(lista);
        }

        return getTaskInstance(lista.stream().filter(condicao).findFirst().orElse(null));
    }

    private static boolean isActiveTask(IEntityTaskInstance tarefa) {
        return tarefa.getEndDate() == null;
    }

    public TaskInstance getUltimaTarefa() {
        return findFirstTaskInstance(true, t -> true);
    }

    public TaskInstance getCurrentTask() {
        return findFirstTaskInstance(true, ProcessInstance::isActiveTask);
    }

    public TaskInstance getTarefaMaisRecenteComNome(final String nomeTipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getTask().getName().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getLatestTask() {
        return findFirstTaskInstance(true, tarefa -> true);
    }

    public TaskInstance getUltimaTarefaConcluidaComNome(final String nomeTipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getEndDate() != null && tarefa.getTask().getName().equalsIgnoreCase(nomeTipo));
    }

    public TaskInstance getUltimaTarefaConcluidaTipo(final MTask<?> tipo) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getEndDate() != null && tarefa.getTask().getAbbreviation().equalsIgnoreCase(tipo.getAbbreviation()));
    }

    public TaskInstance getUltimaTarefaConcluida(final TaskType tipoTarefa) {
        return findFirstTaskInstance(true, tarefa -> tarefa.getEndDate() != null && tarefa.getTask().getType().equals(tipoTarefa));
    }

    protected IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTask, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
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
