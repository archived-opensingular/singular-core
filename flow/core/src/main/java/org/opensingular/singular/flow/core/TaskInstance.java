/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import org.opensingular.singular.flow.core.entity.IEntityCategory;
import org.opensingular.singular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.singular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.singular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.singular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.singular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.singular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.singular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.singular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.singular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.singular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.singular.flow.core.service.IPersistenceService;
import org.opensingular.singular.flow.core.variable.VarInstanceMap;
import org.opensingular.singular.flow.core.view.Lnk;

public class TaskInstance {

    public static final String ALOCACAO = "Alocação";
    public static final String DESALOCACAO = "Desalocação";

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

    public final <X extends IEntityTaskInstance> X getEntityTaskInstance(Integer versionStamp) {
        IEntityTaskInstance e = getEntityTaskInstance();
        if(versionStamp != null){
            if(versionStamp < e.getVersionStamp()){
                throw new RuntimeException("Your Task Version Number is Outdated.");
            }
        }
        return (X) e;
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

    public void relocateTask(MUser author, MUser user,
                             boolean notify, String relocationCause) {
        relocateTask(author, user, notify, relocationCause, null);
    }

    public void relocateTask(MUser author, MUser user,
                             boolean notify, String relocationCause,
                             Integer versionStamp) {
        if (user != null && !isPeople()) {
            throw new SingularFlowException(
                    getProcessInstance().createErrorMsg("A tarefa '" + getName() + "' não pode ser realocada, pois não é do tipo pessoa"));
        }
        MUser pessoaAlocadaAntes = getAllocatedUser();
        if (Objects.equals(user, pessoaAlocadaAntes)) {
            return;
        }

        endLastAllocation();

        getPersistenceService().relocateTask(getEntityTaskInstance(versionStamp), user);

        String trimmedRelocationCause = StringUtils.trimToNull(relocationCause);

        String acao = (user == null) ? DESALOCACAO : ALOCACAO;
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

    public void endLastAllocation() {
        if (isAllocated()) {
            getPersistenceService().endLastAllocation(getEntityTaskInstance());
        }
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

    public boolean isAllocated() {
        return getAllocatedUser() != null;
    }
}
