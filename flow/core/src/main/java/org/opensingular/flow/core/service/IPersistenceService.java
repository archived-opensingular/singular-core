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

package org.opensingular.flow.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.entity.IEntityByCod;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.variable.VarType;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityVariableType;

public interface IPersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition, PROCESS_VERSION extends IEntityProcessVersion, PROCESS_INSTANCE extends IEntityProcessInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, VARIABLE_INSTANCE extends IEntityVariableInstance, ROLE extends IEntityRoleDefinition, ROLE_USER extends IEntityRoleInstance> {

    PROCESS_INSTANCE createProcessInstance(@NotNull PROCESS_VERSION processVersion, @NotNull TASK_VERSION initialState);

    PROCESS_INSTANCE saveProcessInstance(@NotNull PROCESS_INSTANCE instance);

    TASK_INSTANCE addTask(@NotNull PROCESS_INSTANCE instance, @NotNull TASK_VERSION state);

    void completeTask(@NotNull TASK_INSTANCE task, @Nullable String transitionAbbreviation, @Nullable MUser responsibleUser);

    void setProcessInstanceParent(@NotNull PROCESS_INSTANCE instance, @NotNull PROCESS_INSTANCE parentTask);

    ROLE_USER setInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE role, MUser user);

    void removeInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE_USER roleUser);

    Integer updateVariableValue(@NotNull PROCESS_INSTANCE instance, @NotNull VarInstance varInstance, Integer dbVariableCod);

    void setParentTask(@NotNull PROCESS_INSTANCE childrenInstance, @NotNull TASK_INSTANCE parentTask);

    void updateTask(@NotNull TASK_INSTANCE task);

    PROCESS_VERSION retrieveProcessVersionByCod(@NotNull Integer cod);

    PROCESS_INSTANCE retrieveProcessInstanceByCod(@NotNull Integer cod);

    TASK_INSTANCE retrieveTaskInstanceByCod(@NotNull Integer cod);

    IEntityTaskInstanceHistory saveTaskHistoricLog(@NotNull TASK_INSTANCE task, String typeDescription, String detail, MUser allocatedUser,
            MUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance);

    void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TASK_INSTANCE originTask, TASK_INSTANCE destinationTask, VarInstanceMap<?> instanceMap);

    default void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {
        saveVariableHistoric(dateHour, instance, originTask != null ? originTask.<TASK_INSTANCE> getEntityTaskInstance() : null, destinationTask != null ? destinationTask.<TASK_INSTANCE> getEntityTaskInstance() : null, instanceMap);
    }

    List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods);

    /**
     * Must persist: {@link IEntityProcessDefinition}, {@link IEntityProcessVersion},
     * {@link IEntityTaskDefinition}, {@link IEntityTaskVersion} and {@link IEntityTaskTransitionVersion}.
     *
     * @param processVersion the process definition to persist.
     * @return the persisted process definition.
     */
    PROCESS_VERSION saveProcessVersion(PROCESS_VERSION processVersion);

    IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType);

    void relocateTask(TASK_INSTANCE taskInstance, MUser user);

    void updateTargetEndDate(TASK_INSTANCE taskInstance, Date targetEndDate);

    void refreshModel(IEntityByCod model);

    void flushSession();

    void commitTransaction();

    // Consultas
    List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEF processVersion, @Nullable Date beginDate,
            @Nullable Date endDate, @Nullable Collection<? extends TASK_DEF> states);

    List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEF processVersion, @Nullable MUser creatingUser,
            @Nullable Boolean active);

    void endLastAllocation(TASK_INSTANCE entityTaskInstance);
}
