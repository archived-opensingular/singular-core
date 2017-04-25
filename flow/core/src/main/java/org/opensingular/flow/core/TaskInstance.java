/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.lib.commons.net.Lnk;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class TaskInstance {

    public static final String ALOCACAO = "Alocação";
    public static final String DESALOCACAO = "Desalocação";

    private IEntityTaskInstance entityTask;

    private ProcessInstance processInstance;

    private transient STask<?> flowTask;

    TaskInstance(@Nonnull ProcessInstance processInstance, @Nonnull IEntityTaskInstance task) {
        Objects.requireNonNull(processInstance);
        Objects.requireNonNull(task);
        if (!processInstance.getEntity().equals(task.getProcessInstance())) {
            throw new SingularFlowException(processInstance.createErrorMsg(
                    "O objeto " + task.getClass().getSimpleName() + " " + task + " não é uma tarefa filha do objeto " +
                            processInstance.getClass().getSimpleName() + " em questão"), processInstance);
        }
        this.processInstance = processInstance;
        this.entityTask = task;
    }

    TaskInstance(@Nonnull IEntityTaskInstance task) {
        this.entityTask = Objects.requireNonNull(task);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <X extends ProcessInstance> X getProcessInstance() {
        if (processInstance == null) {
            processInstance = Flow.getProcessInstance(entityTask.getProcessInstance());
        }
        return (X) processInstance;
    }

    @Nonnull
    public Optional<STask<?>> getFlowTask() {
        if (flowTask == null) {
            flowTask = getProcessInstance().getProcessDefinition().getFlowMap().getTaskByAbbreviation(getTaskVersion().getAbbreviation()).orElse(null);
        }
        return Optional.ofNullable(flowTask);
    }

    @Nonnull
    public STask<?> getFlowTaskOrException() {
        return getFlowTask().orElseThrow(() -> new SingularFlowException(
                "Era esperado encontra a definição para a entidade de tarefa, mas não há correspondente entre o BD e " +
                        "a definição do processo",
                this));
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

    public SUser getAllocatedUser() {
        return entityTask.getAllocatedUser();
    }

    public SUser getResponsibleUser() {
        return entityTask.getResponsibleUser();
    }

    public Date getTargetEndDate() {
        return entityTask.getTargetEndDate();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public final <X extends IEntityTaskInstance> X getEntityTaskInstance() {
        entityTask = getPersistenceService().retrieveTaskInstanceByCodOrException(entityTask.getCod());
        return (X) entityTask;
    }

    public final <X extends IEntityTaskInstance> X getEntityTaskInstance(Integer versionStamp) {
        IEntityTaskInstance e = getEntityTaskInstance();
        if(versionStamp != null){
            if(versionStamp < e.getVersionStamp()){
                throw new SingularFlowException("Your Task Version Number is Outdated.", this);
            }
        }
        return (X) e;
    }

    private IEntityTaskVersion getTaskVersion() {
        return entityTask.getTaskVersion();
    }

    @Nonnull
    public String getName() {
        return getFlowTask().map(STask::getName).orElseGet(() -> getTaskVersion().getName());
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
        return getFlowTask().map(STask::isEnd).orElseGet(() -> getTaskVersion().isEnd());
    }

    public boolean isPeople() {
        return getFlowTask().map(STask::isPeople).orElseGet(() -> getTaskVersion().isPeople());
    }

    public boolean isWait() {
        return getFlowTask().map(STask::isWait).orElseGet(() -> getTaskVersion().isWait());
    }

    /** Verifica se o tipo da tarefa corresponde ao informado. */
    public boolean isAtTask(@Nonnull ITaskDefinition expectedTaskType) {
        Objects.requireNonNull(expectedTaskType);
        Optional<STask<?>> taskType = getFlowTask();
        if (taskType.isPresent()) {
            return taskType.get().is(expectedTaskType);
        }
        return getAbbreviation().equalsIgnoreCase(expectedTaskType.getKey());
    }

    /** Verifica se o tipo da tarefa corresponde a sigla informada. */
    public boolean isAtTask(@Nonnull String expectedTaskTypeAbbreviation) {
        return getAbbreviation().equalsIgnoreCase(expectedTaskTypeAbbreviation);
    }

    /** Prepara para execução a transação default da instância. Senão existir transição default, dispara exception. */
    @Nonnull
    public TransitionCall prepareTransition() {
        return new TransitionCall(
                new TransitionRef(this, getFlowTaskOrException().resolveDefaultTransitionOrException()));
    }

    /**
     * Prepara para execução a transação da instancia correspodente ao nome infomado. Senão existir transição com o nome
     * informado, dispara exception.
     */
    @Nonnull
    public TransitionCall prepareTransition(@Nonnull String transitionName) {
        Objects.requireNonNull(transitionName);
        return new TransitionCall(
                new TransitionRef(this, getFlowTaskOrException().getTransitionOrException(transitionName)));
    }

    public void relocateTask(SUser author, SUser user,
                             boolean notify, String relocationCause) {
        relocateTask(author, user, notify, relocationCause, null);
    }

    public void relocateTask(SUser author, SUser user,
                             boolean notify, String relocationCause,
                             Integer versionStamp) {
        if (user != null && !isPeople()) {
            throw new SingularFlowException(
                    "A tarefa '" + getName() + "' não pode ser realocada, pois não é do tipo pessoa", this);
        }
        SUser pessoaAlocadaAntes = getAllocatedUser();
        if (Objects.equals(user, pessoaAlocadaAntes)) {
            return;
        }

        IEntityTaskInstance entityTaskInstance = getEntityTaskInstance(versionStamp);

        endLastAllocation();

        getPersistenceService().relocateTask(entityTaskInstance, user);

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

        IEntityProcessInstance childProcessInstanceEntity = childProcessInstance.getEntity();

        getPersistenceService().setParentTask(childProcessInstanceEntity, entityTask);

        if (historyType != null) {
            log(historyType, childProcessInstanceEntity.getDescription(),
                    childProcessInstance.getCurrentTaskOrException().getAllocatedUser()).sendEmail();
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
    @Nonnull
    public List<ProcessInstance> getChildProcesses() {
        return Flow.getProcessInstances(entityTask.getChildProcesses());
    }

    private void notifyStateUpdate() {
        Flow.notifyListeners(n -> n.notifyStateUpdate(getProcessInstance()));
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento) {
        return log(tipoHistorico, detalhamento, null, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, SUser alocada) {
        return log(tipoHistorico, detalhamento, alocada, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, SUser alocada, SUser autor, Date dataHora) {
        return log(tipoHistorico, detalhamento, alocada, autor, dataHora, null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, SUser alocada, SUser autor, Date dataHora,
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
            SUser p = getAllocatedUser();
            if (p != null) {
                sb.append(" (").append(p.getSimpleName()).append(')');
            }
        }
        return sb;

    }

    /**
     * Retorna a lista de usuário diretamente responsáveis pela tarefa. Pode retorna uma lista vazia se a tarefa não
     * tive nenhum responsavel direto ou nao fizer sentido ter responsável direto (ex.: task Java).
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public List<SUser> getDirectlyResponsibles() {
        SUser allocatedUser = getAllocatedUser();
        if (allocatedUser != null) {
            return ImmutableList.of(allocatedUser);
        }
        return getFlowTask()
                .filter(task -> task.isPeople() || (task.isWait() && task.getAccessStrategy() != null))
                .map(task -> (List<SUser>) getPersistenceService().retrieveUsersByCod(getFirstLevelUsersCodWithAccess(task)))
                .orElse(Collections.emptyList());
    }

    private Set<Integer> getFirstLevelUsersCodWithAccess(@Nonnull STask<?> flowTask) {

        TaskAccessStrategy<ProcessInstance> accessStrategy = flowTask.getAccessStrategy();
        IEntityTaskVersion taskVersion = getTaskVersion();
        String abbreviation = taskVersion.getAbbreviation();
        ProcessInstance processInstance = getProcessInstance();

        Objects.requireNonNull(flowTask, "Task com a sigla " + abbreviation + " não encontrada na definição " + processInstance.getProcessDefinition().getName());
        Objects.requireNonNull(accessStrategy,"Estratégia de acesso da task " + abbreviation + " não foi definida");

        return accessStrategy.getFirstLevelUsersCodWithAccess(processInstance);
    }

    public boolean isAllocated() {
        return getAllocatedUser() != null;
    }
}
