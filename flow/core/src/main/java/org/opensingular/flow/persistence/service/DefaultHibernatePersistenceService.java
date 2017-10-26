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

package org.opensingular.flow.persistence.service;

import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityTaskHistoricType;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.FlowDefinitionEntity;
import org.opensingular.flow.persistence.entity.FlowInstanceEntity;
import org.opensingular.flow.persistence.entity.FlowVersionEntity;
import org.opensingular.flow.persistence.entity.RoleDefinitionEntity;
import org.opensingular.flow.persistence.entity.RoleInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskHistoricTypeEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.entity.VariableInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableTypeInstance;
import org.opensingular.flow.persistence.entity.util.SessionLocator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultHibernatePersistenceService extends
        AbstractHibernatePersistenceService<CategoryEntity, FlowDefinitionEntity, FlowVersionEntity, FlowInstanceEntity,
                TaskInstanceEntity,
                TaskDefinitionEntity,
                TaskVersionEntity,
                VariableInstanceEntity,
                RoleDefinitionEntity,
                RoleInstanceEntity> {

    public DefaultHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    // -------------------------------------------------------
    // FlowInstance
    // -------------------------------------------------------

    @Override
    protected FlowInstanceEntity newFlowInstance(FlowVersionEntity flowVersion) {
        FlowInstanceEntity flowInstance = new FlowInstanceEntity();
        flowInstance.setFlowVersion(flowVersion);
        flowInstance.setRoles(new ArrayList<>());
        return flowInstance;
    }

    @Override
    protected RoleInstanceEntity newEntityRole(FlowInstanceEntity instance, RoleDefinitionEntity role, SUser user, SUser allocator) {
        SUser resolvedUser = saveUserIfNeeded(user);
        final RoleInstanceEntity entityRole = new RoleInstanceEntity();
        entityRole.setFlowInstance(instance);
        entityRole.setUser((Actor) resolvedUser);
        entityRole.setRole(role);
        entityRole.setAllocatorUser((Actor) allocator);
        entityRole.setCreateDate(new Date());
        return entityRole;
    }

    // -------------------------------------------------------
    // Task
    // -------------------------------------------------------

    @Override
    protected Class<TaskInstanceEntity> getClassTaskInstance() {
        return TaskInstanceEntity.class;
    }

    @Override
    protected TaskInstanceEntity newTaskInstance(FlowInstanceEntity flowInstance, TaskVersionEntity taskVersion) {
        TaskInstanceEntity taskInstance = new TaskInstanceEntity();
        taskInstance.setFlowInstance(flowInstance);
        taskInstance.setTask(taskVersion);
        return taskInstance;
    }

    @Override
    protected IEntityTaskInstanceHistory newTaskInstanceHistory(TaskInstanceEntity task, IEntityTaskHistoricType taskHistoryType,
            SUser allocatedUser, SUser responsibleUser) {

        TaskInstanceHistoryEntity history = new TaskInstanceHistoryEntity();
        history.setTaskInstance(task);
        history.setType((TaskHistoricTypeEntity) taskHistoryType);
        history.setAllocatedUser((Actor) allocatedUser);
        history.setAllocatorUser((Actor) responsibleUser);
        return history;
    }

    @Override
    protected Class<? extends TaskHistoricTypeEntity> getClassEntityTaskHistoricType() {
        return TaskHistoricTypeEntity.class;
    }

    @Override
    public FlowVersionEntity retrieveFlowVersionByCod(Integer cod) {
        return getSession().refreshByPk(FlowVersionEntity.class, cod);
    }

    // -------------------------------------------------------
    // Variable
    // -------------------------------------------------------

    @Override
    @Nonnull
    protected Optional<VariableInstanceEntity> retrieveVariableInstanceByCod(@Nonnull Integer cod) {
        return getSession().retrieve(VariableInstanceEntity.class, Objects.requireNonNull(cod));
    }

    @Override
    protected VariableInstanceEntity newVariableInstance(FlowInstanceEntity flowInstance, String name) {
        VariableInstanceEntity variable = new VariableInstanceEntity();
        variable.setFlowInstance(flowInstance);
        variable.setName(name);
        return variable;
    }


    @Override
    protected IEntityExecutionVariable newExecutionVariable(FlowInstanceEntity instance, IEntityVariableInstance flowInstanceVar,
            TaskInstanceEntity originTask, TaskInstanceEntity destinationTask, IEntityVariableType type) {
        ExecutionVariableEntity newEntity = new ExecutionVariableEntity();
        newEntity.setVariable((VariableInstanceEntity) flowInstanceVar);
        newEntity.setFlowInstance(instance);
        newEntity.setOriginTask(originTask);
        newEntity.setDestinationTask(destinationTask);
        newEntity.setType((VariableTypeInstance) type);
        return newEntity;
    }

    @Override
    protected Class<? extends IEntityVariableType> getClassEntityVariableType() {
        return VariableTypeInstance.class;
    }

    @Override
    protected Class<FlowInstanceEntity> getClassFlowInstance() {
        return FlowInstanceEntity.class;
    }
    
    // -------------------------------------------------------
    // Listagens
    // -------------------------------------------------------

    @Override
    public List<? extends SUser> retrieveUsersByCod(Collection<Integer> cods) {
        return Collections.emptyList();
    }

}
