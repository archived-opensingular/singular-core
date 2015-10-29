package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.view.Lnk;

public class TaskInstance {

    private IEntityTaskInstance entityTask;

    private ProcessInstance processInstance;

    private transient MTask<?> flowTask;

    TaskInstance(ProcessInstance processInstance, IEntityTaskInstance task) {
        if (!processInstance.getEntity().equals(task.getProcessInstance())) {
            throw new SingularFlowException(
                    processInstance.createErrorMsg("O objeto IDadosTarefa " + task + " não é filho do objeto IDadosInstancia em questão"));
        }
        this.processInstance = processInstance;
        this.entityTask = task;
    }

    TaskInstance(IEntityTaskInstance task) {
        this.entityTask = task;
    }

    @SuppressWarnings("unchecked")
    public <X extends ProcessInstance> X getProcessInstance() {
        if (processInstance == null) {
            processInstance = Flow.getProcessInstance(entityTask.getProcessInstance());
        }
        return (X) processInstance;
    }

    public MTask<?> getFlowTask() {
        if (flowTask == null) {
            flowTask = getProcessInstance().getProcessDefinition().getFlowMap().getTaskBybbreviation(getTaskVersion().getAbbreviation());
        }
        return flowTask;
    }

    public Serializable getId() {
        return entityTask.getCod();
    }

    public String getFullId() {
        return Flow.generateID(this);
    }

    public Lnk getDefaultHref() {
        return Flow.getDefaultHrefFor(this);
    }

    public MUser getAllocatedUser() {
        return entityTask.getAllocatedUser();
    }

    public MUser getResponsibleUser() {
        return entityTask.getResponsibleUser();
    }

    public Date getTargetEndDate() {
        return entityTask.getTargetEndDate();
    }

    @SuppressWarnings("unchecked")
    public final <X extends IEntityTaskInstance> X getEntityTaskInstance() {
        entityTask = getPersistenceService().retrieveTaskInstanceByCod(entityTask.getCod());
        return (X) entityTask;
    }

    private IEntityTaskVersion getTaskVersion() {
        return entityTask.getTask();
    }

    public String getName() {
        if (getFlowTask() != null) {
            return getFlowTask().getName();
        }
        return getTaskVersion().getName();
    }

    public String getAbbreviation() {
        return getTaskVersion().getAbbreviation();
    }

    public String getProcessName() {
        return getProcessInstance().getProcessName();
    }

    public String getTaskName() {
        return getName();
    }

    public String getDescricao() {
        return getProcessInstance().getDescription();
    }

    public Date getBeginDate() {
        return entityTask.getBeginDate();
    }

    public Date getEndDate() {
        return entityTask.getEndDate();
    }


    public boolean isFinished() {
        return entityTask.isFinished();
    }

    public boolean isActive() {
        return entityTask.isActive();
    }

    public boolean isEnd() {
        if (getFlowTask() != null) {
            return getFlowTask().isEnd();
        }
        return getTaskVersion().isEnd();
    }

    public boolean isPeople() {
        if (getFlowTask() != null) {
            return getFlowTask().isPeople();
        }
        return getTaskVersion().isPeople();
    }

    public boolean isWait() {
        if (getFlowTask() != null) {
            return getFlowTask().isWait();
        }
        return getTaskVersion().isWait();
    }

    // TODO Daniel: Existe duas formas de fazer uma transicao. O método abaixo e
    // executeTransitaion(). Decidir por apenas um ficar público. Sugiro o
    // prepareTransition
    public TransitionCall prepareTransition(String transitionName) {
        return new TransitionCallImpl(getTransition(transitionName));
    }

    public TransitionRef getTransition(String transitionName) {
        return new TransitionRef(this, getFlowTask().getTransicaoOrException(transitionName));
    }

    public void relocateTask(MUser author, MUser user, boolean notify, String relocationCause) {
        if (user != null && !isPeople()) {
            throw new SingularFlowException(
                    getProcessInstance().createErrorMsg("A tarefa '" + getName() + "' não pode ser realocada, pois não é do tipo pessoa"));
        }
        MUser pessoaAlocadaAntes = getAllocatedUser();
        if (Objects.equals(user, pessoaAlocadaAntes)) {
            return;
        }

        getPersistenceService().relocateTask(getEntityTaskInstance(), user);

        String trimmedRelocationCause = StringUtils.trimToNull(relocationCause);

        String acao = (user == null) ? "Desalocação" : "Alocação";
        if (author == null) {
            log(acao + " Automática", trimmedRelocationCause, user, null, new Date());
        } else {
            log(acao, trimmedRelocationCause, user, author, new Date());
        }

        if (notify) {
            Flow.notifyListeners(n -> n.notifyUserTaskRelocation(this, author, pessoaAlocadaAntes, user, pessoaAlocadaAntes));
            Flow.notifyListeners(n -> n.notifyUserTaskAllocation(this, author, user, user, pessoaAlocadaAntes, trimmedRelocationCause));
        }

        notifyStateUpdate();
    }

    public void setTargetEndDate(Date targetEndDate) {
        getPersistenceService().updateTargetEndDate(getEntityTaskInstance(), targetEndDate);
    }

    public void createSubTask(String historyType, ProcessInstance childProcessInstance) {
        getPersistenceService().setParentTask(childProcessInstance.getEntity(), entityTask);

        if (historyType != null) {
            log(historyType, childProcessInstance.getEntity().getDescription(), childProcessInstance.getCurrentTask().getAllocatedUser())
                    .sendEmail();
        }
        notifyStateUpdate();
    }

    /**
     * Retorna todos os processo filhos associados a essa tarefa. Podem ser
     * processo disparados em conjunto a tarefa atual ou mesmo um processo filho
     * que consiste no subProcesso da tarefa.
     *
     * @return sempre diferente de null, mas pode ser lista vazia.
     */
    public List<ProcessInstance> getChildProcesses() {
        return Flow.getProcessInstances(entityTask.getChildProcesses());
    }

    private void notifyStateUpdate() {
        Flow.notifyListeners(n -> n.notifyStateUpdate(getProcessInstance()));
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento) {
        return log(tipoHistorico, detalhamento, null, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada) {
        return log(tipoHistorico, detalhamento, alocada, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada, MUser autor, Date dataHora) {
        return log(tipoHistorico, detalhamento, alocada, autor, dataHora, null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada, MUser autor, Date dataHora,
            IEntityProcessInstance demandaFilha) {
        IEntityTaskInstanceHistory historico = getPersistenceService().saveTaskHistoricLog(entityTask, tipoHistorico, detalhamento, alocada,
                autor, dataHora, demandaFilha);
        return new TaskHistoricLog(historico);
    }

    private IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return getProcessInstance().getProcessDefinition().getPersistenceService();
    }

    public StringBuilder getDescricaoExtendida(boolean adicionarAlocado) {
        StringBuilder sb = new StringBuilder(250);
        sb.append(getProcessInstance().getProcessName()).append(" - ").append(getName());
        String descricao = getProcessInstance().getDescription();
        if (descricao != null) {
            sb.append(" - ").append(descricao);
        }
        if (adicionarAlocado) {
            MUser p = getAllocatedUser();
            if (p != null) {
                sb.append(" (").append(p.getSimpleName()).append(")");
            }
        }
        return sb;

    }

    @SuppressWarnings("unchecked")
    public List<MUser> getDirectlyResponsibles() {
        if (getAllocatedUser() != null) {
            return ImmutableList.of(getAllocatedUser());
        }
        if (getFlowTask() != null && (getFlowTask().isPeople() || (getFlowTask().isWait() && getFlowTask().getAccessStrategy() != null))) {
            Set<Integer> codPessoas = getFirstLevelUsersCodWithAccess();
            return (List<MUser>) getPersistenceService().retrieveUsersByCod(codPessoas);
        }
        return Collections.emptyList();
    }

    private Set<Integer> getFirstLevelUsersCodWithAccess() {
        Objects.requireNonNull(getFlowTask(), "Task com a sigla " + getTaskVersion().getAbbreviation() + " não encontrada na definição "
                + getProcessInstance().getProcessDefinition().getName());
        Objects.requireNonNull(getFlowTask().getAccessStrategy(),
                "Estratégia de acesso da task " + getTaskVersion().getAbbreviation() + " não foi definida");
        return getFlowTask().getAccessStrategy().getFirstLevelUsersCodWithAccess(getProcessInstance());
    }

    public void executeTransition() {
        FlowEngine.executeTransition(this, null, null);
    }

    public void executeTransition(String destino) {
        FlowEngine.executeTransition(this, destino, null);
    }

    public void executeTransition(String destino, VarInstanceMap<?> param) {
        FlowEngine.executeTransition(this, destino, param);
    }
}
