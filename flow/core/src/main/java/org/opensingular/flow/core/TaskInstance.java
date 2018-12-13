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
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
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
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class TaskInstance implements Serializable {

    public static final String ALLOCATE = "Alocação";
    public static final String DEALLOCATE = "Desalocação";
    public static final String TASK_VISUALIZATION = "Leitura da tarefa";

    private final Integer taskCod;

    private transient IEntityTaskInstance entityTask;

    private transient FlowInstance flowInstance;

    private transient STask<?> flowTask;

    TaskInstance(@Nonnull FlowInstance flowInstance, @Nonnull IEntityTaskInstance task) {
        this(task);
        Objects.requireNonNull(flowInstance);
        if (!flowInstance.getEntity().equals(task.getFlowInstance())) {
            throw new SingularFlowException(flowInstance.createErrorMsg(
                    "O objeto " + task.getClass().getSimpleName() + " " + task + " não é uma tarefa filha do objeto " +
                            flowInstance.getClass().getSimpleName() + " em questão"), flowInstance);
        }
        this.flowInstance = flowInstance;
    }

    TaskInstance(@Nonnull IEntityTaskInstance task) {
        this.entityTask = Objects.requireNonNull(task);
        this.taskCod = Objects.requireNonNull(task.getCod());
    }

    @Nonnull
    public STransition findTransition(@Nullable String transitionName) {
        if (transitionName == null) {
            return getFlowTaskOrException().resolveDefaultTransitionOrException();
        }
        return getFlowTaskOrException().getTransitionOrException(transitionName);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <X extends FlowInstance> X getFlowInstance() {
        if (flowInstance == null) {
            flowInstance = Flow.getFlowInstance(getEntity().getFlowInstance());
        }
        return (X) flowInstance;
    }


    private IEntityTaskInstance getEntity() {
        if (entityTask == null) {
            entityTask = Flow.getConfigBean().getPersistenceService().retrieveTaskInstanceByCodOrException(taskCod);
        }
        return entityTask;
    }

    @Nonnull
    public Optional<STask<?>> getFlowTask() {
        if (flowTask == null) {
            flowTask = getFlowInstance().getFlowDefinition().getFlowMap().getTaskByAbbreviation(getTaskVersion().getAbbreviation()).orElse(null);
        }
        return Optional.ofNullable(flowTask);
    }

    @Nonnull
    public STask<?> getFlowTaskOrException() {
        return getFlowTask().orElseThrow(() -> new SingularFlowException(
                "Era esperado encontra a definição para a entidade de tarefa, mas não há correspondente entre o BD e " +
                        "a definição do fluxo",
                this));
    }

    public Serializable getId() {
        return taskCod;
    }

    public String getFullId() {
        return Flow.generateID(this);
    }

    public Lnk getDefaultHref() {
        return Flow.getDefaultHrefFor(this);
    }

    public SUser getAllocatedUser() {
        return getEntity().getAllocatedUser();
    }

    public SUser getResponsibleUser() {
        return getEntity().getResponsibleUser();
    }

    public Date getTargetEndDate() {
        return getEntity().getTargetEndDate();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public final <X extends IEntityTaskInstance> X getEntityTaskInstance() {
        entityTask = getPersistenceService().retrieveTaskInstanceByCodOrException(taskCod);
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
        return getEntity().getTaskVersion();
    }

    @Nonnull
    public String getName() {
        return getFlowTask().map(STask::getName).orElseGet(() -> getTaskVersion().getName());
    }

    public String getAbbreviation() {
        return getTaskVersion().getAbbreviation();
    }

    public String getFlowName() {
        return getFlowInstance().getFlowName();
    }

    public String getTaskName() {
        return getName();
    }

    public String getDescription() {
        return getFlowInstance().getDescription();
    }

    public Date getBeginDate() {
        return getEntity().getBeginDate();
    }

    public Date getEndDate() {
        return getEntity().getEndDate();
    }


    public boolean isFinished() {
        return getEntity().isFinished();
    }

    public boolean isActive() {
        return getEntity().isActive();
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
        return new TransitionCall(new RefTransition(this));
    }

    /**
     * Retorna empty se a transição não existir ou se nenhuma transição tiver sido executada.
     * @return
     */
    public Optional<STransition> getExecutedTransition(){
        if (isFinished() && getEntity().getExecutedTransition() != null) {
            return getFlowTaskOrException().getTransition(entityTask.getExecutedTransition().getName());
        }
        return Optional.empty();
    }

    /**
     * Prepara para execução a transação da instancia correspodente ao nome infomado. Senão existir transição com o nome
     * informado, dispara exception.
     */
    @Nonnull
    public TransitionCall prepareTransition(@Nonnull String transitionName) {
        Objects.requireNonNull(transitionName);
        return new TransitionCall(new RefTransition(this, transitionName));
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
        SUser userAllocatedBefore = getAllocatedUser();
        if (Objects.equals(user, userAllocatedBefore)) {
            return;
        }

        IEntityTaskInstance entityTaskInstance = getEntityTaskInstance(versionStamp);

        endLastAllocation();

        getPersistenceService().relocateTask(entityTaskInstance, user);

        String trimmedRelocationCause = StringUtils.trimToNull(relocationCause);

        String action = (user == null) ? DEALLOCATE : ALLOCATE;
        if (author == null) {
            log(action + " Automática", trimmedRelocationCause, user, null, new Date());
        } else {
            log(action, trimmedRelocationCause, user, author, new Date());
        }

        if (notify) {
            Flow.notifyListeners(n -> n.notifyUserTaskRelocation(this, author, userAllocatedBefore, user, userAllocatedBefore));
            Flow.notifyListeners(n -> n.notifyUserTaskAllocation(this, author, user, user, userAllocatedBefore, trimmedRelocationCause));
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

    public void createSubTask(String historyType, FlowInstance childFlowInstance) {

        IEntityFlowInstance childFlowInstanceEntity = childFlowInstance.getEntity();

        getPersistenceService().setParentTask(childFlowInstanceEntity, getEntity());

        if (historyType != null) {
            log(historyType, childFlowInstanceEntity.getDescription(),
                    childFlowInstance.getCurrentTaskOrException().getAllocatedUser()).sendEmail();
        }

        notifyStateUpdate();
    }

    /**
     * Retorna todos os fluxos filhos associados a essa tarefa. Podem ser
     * fluxo disparados em conjunto a tarefa atual ou mesmo um fluxo filho
     * que consiste no sub fluxo da tarefa.
     *
     * @return sempre diferente de null, mas pode ser lista vazia.
     */
    @Nonnull
    public List<FlowInstance> getChildFlows() {
        return Flow.getFlowInstances(getEntity().getChildFlows());
    }

    private void notifyStateUpdate() {
        Flow.notifyListeners(n -> n.notifyStateUpdate(getFlowInstance()));
    }

    public TaskHistoricLog log(String logType, String description) {
        return log(logType, description, null, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String logType, String description, SUser allocatedUser) {
        return log(logType, description, allocatedUser, Flow.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String logType, String description, SUser allocatedUser, SUser author, Date dateTime) {
        return log(logType, description, allocatedUser, author, dateTime, null);
    }

    public TaskHistoricLog log(String logType, String description, SUser allocatedUser, SUser author, Date dateTime,
            IEntityFlowInstance childInstance) {
        IEntityTaskInstanceHistory history = getPersistenceService().saveTaskHistoricLog(getEntity(), logType,
                description, allocatedUser, author, dateTime, childInstance);
        return new TaskHistoricLog(history);
    }

    private IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion,
            IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return getFlowInstance().getFlowDefinition().getPersistenceService();
    }

    public StringBuilder getExtendedDescription(boolean addAllocated) {
        StringBuilder sb = new StringBuilder(250);
        sb.append(getFlowInstance().getFlowName()).append(" - ").append(getName());
        String description = getFlowInstance().getDescription();
        if (description != null) {
            sb.append(" - ").append(description);
        }
        if (addAllocated) {
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
    public List<SUser> getDirectlyResponsible() {
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

        TaskAccessStrategy<FlowInstance> accessStrategy = flowTask.getAccessStrategy();
        IEntityTaskVersion taskVersion = getTaskVersion();
        String abbreviation = taskVersion.getAbbreviation();
        FlowInstance                     flowInstance   = getFlowInstance();

        Objects.requireNonNull(flowTask, "Task com a sigla " + abbreviation + " não encontrada na definição " + flowInstance.getFlowDefinition().getName());
        Objects.requireNonNull(accessStrategy,"Estratégia de acesso da task " + abbreviation + " não foi definida");

        return accessStrategy.getFirstLevelUsersCodWithAccess(flowInstance);
    }

    public boolean isAllocated() {
        return getAllocatedUser() != null;
    }
}
