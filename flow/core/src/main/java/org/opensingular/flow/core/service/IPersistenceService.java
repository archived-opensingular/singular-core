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

import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.entity.IEntityByCod;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.variable.VarType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IPersistenceService<DEFINITION_CATEGORY extends IEntityCategory, FLOW_DEFINITION extends IEntityFlowDefinition, FLOW_VERSION extends IEntityFlowVersion, FLOW_INSTANCE extends IEntityFlowInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, VARIABLE_INSTANCE extends IEntityVariableInstance, ROLE extends IEntityRoleDefinition, ROLE_USER extends IEntityRoleInstance> {

    FLOW_INSTANCE createFlowInstance(@NotNull FLOW_VERSION flowVersion, @NotNull TASK_VERSION initialState);

    FLOW_INSTANCE saveFlowInstance(@NotNull FLOW_INSTANCE instance);

    TASK_INSTANCE addTask(@NotNull FLOW_INSTANCE instance, @NotNull TASK_VERSION state);

    void completeTask(@NotNull TASK_INSTANCE task, @Nullable String transitionAbbreviation, @Nullable SUser responsibleUser);

    void setFlowInstanceParent(@NotNull FLOW_INSTANCE instance, @NotNull FLOW_INSTANCE parentTask);

    ROLE_USER setInstanceUserRole(@NotNull FLOW_INSTANCE instance, ROLE role, SUser user);

    void removeInstanceUserRole(@NotNull FLOW_INSTANCE instance, ROLE_USER roleUser);

    @Nullable
    Integer updateVariableValue(@NotNull FLOW_INSTANCE instance, @NotNull VarInstance varInstance, @Nullable Integer dbVariableCod);

    void setParentTask(@NotNull FLOW_INSTANCE childrenInstance, @NotNull TASK_INSTANCE parentTask);

    void updateTask(@NotNull TASK_INSTANCE task);

    FLOW_VERSION retrieveFlowVersionByCod(@NotNull Integer cod);

    @Nonnull
    Optional<FLOW_INSTANCE> retrieveFlowInstanceByCod(@NotNull Integer cod);

    @Nonnull
    default FLOW_INSTANCE retrieveFlowInstanceByCodOrException(@NotNull Integer cod) {
        return retrieveFlowInstanceByCod(cod).orElseThrow(
                () -> new SingularFlowException("Nao foi encontrada a instancia de fluxo cod=" + cod));
    }

    @Nonnull
    Optional<TASK_INSTANCE> retrieveTaskInstanceByCod(@NotNull Integer cod);

    default TASK_INSTANCE retrieveTaskInstanceByCodOrException(@NotNull Integer cod) {
        return retrieveTaskInstanceByCod(cod).orElseThrow(
                () -> new SingularFlowException("Nao foi encontrada a instancia de tarefa cod=" + cod));
    }

    IEntityTaskInstanceHistory saveTaskHistoricLog(@NotNull TASK_INSTANCE task, String typeDescription, String detail, SUser allocatedUser,
            SUser responsibleUser, Date dateHour, FLOW_INSTANCE generatedFlowInstance);

    void saveVariableHistoric(Date dateHour, FLOW_INSTANCE instance, TASK_INSTANCE originTask, TASK_INSTANCE destinationTask, VarInstanceMap<?,?> instanceMap);

    default void saveVariableHistoric(Date dateHour, FLOW_INSTANCE instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?,?> instanceMap) {
        saveVariableHistoric(dateHour, instance, originTask != null ? originTask.<TASK_INSTANCE> getEntityTaskInstance() : null, destinationTask != null ? destinationTask.<TASK_INSTANCE> getEntityTaskInstance() : null, instanceMap);
    }

    List<? extends SUser> retrieveUsersByCod(Collection<Integer> cods);

    /**
     * Must persist: {@link IEntityFlowDefinition}, {@link IEntityFlowVersion},
     * {@link IEntityTaskDefinition}, {@link IEntityTaskVersion} and {@link IEntityTaskTransitionVersion}.
     *
     * @param flowVersion the flow definition to persist.
     * @return the persisted flow definition.
     */
    FLOW_VERSION saveFlowVersion(FLOW_VERSION flowVersion);

    IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType);

    void relocateTask(TASK_INSTANCE taskInstance, SUser user);

    void updateTargetEndDate(TASK_INSTANCE taskInstance, Date targetEndDate);

    void refreshModel(IEntityByCod model);

    void flushSession();

    void commitTransaction();

    // Consultas
    List<FLOW_INSTANCE> retrieveFlowInstancesWith(@NotNull FLOW_DEFINITION flowDefinition, @Nullable Date beginDate,
            @Nullable Date endDate, @Nullable Collection<? extends TASK_DEF> states);

    List<FLOW_INSTANCE> retrieveFlowInstancesWith(@NotNull FLOW_DEFINITION flowDefinition, @Nullable SUser creatingUser,
            @Nullable Boolean active);

    void endLastAllocation(TASK_INSTANCE entityTaskInstance);
}
