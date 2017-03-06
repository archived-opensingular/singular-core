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

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.entity.*;
import org.opensingular.flow.persistence.entity.*;
import org.opensingular.flow.persistence.entity.util.SessionLocator;

import javax.annotation.Nonnull;
import java.util.*;

public class DefaultHibernatePersistenceService extends
        AbstractHibernatePersistenceService<CategoryEntity, ProcessDefinitionEntity,
                ProcessVersionEntity,
                ProcessInstanceEntity,
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
    // ProcessIntance
    // -------------------------------------------------------

    @Override
    protected ProcessInstanceEntity newProcessInstance(ProcessVersionEntity processVersion) {
        ProcessInstanceEntity processInstance = new ProcessInstanceEntity();
        processInstance.setProcessVersion(processVersion);
        processInstance.setRoles(new ArrayList<>());
        return processInstance;
    }

    @Override
    protected RoleInstanceEntity newEntityRole(ProcessInstanceEntity instance, RoleDefinitionEntity role, MUser user, MUser allocator) {
        user = saveUserIfNeeded(user);
        final RoleInstanceEntity entityRole = new RoleInstanceEntity();
        entityRole.setProcessInstance(instance);
        entityRole.setUser((Actor) user);
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
    protected TaskInstanceEntity newTaskInstance(ProcessInstanceEntity processInstance, TaskVersionEntity taskVersion) {
        TaskInstanceEntity taskInstance = new TaskInstanceEntity();
        taskInstance.setProcessInstance(processInstance);
        taskInstance.setTask(taskVersion);
        return taskInstance;
    }

    @Override
    protected IEntityTaskInstanceHistory newTaskInstanceHistory(TaskInstanceEntity task, IEntityTaskHistoricType taskHistoryType,
            MUser allocatedUser, MUser responsibleUser) {

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

    // -------------------------------------------------------
    // Process Definition e Version
    // -------------------------------------------------------

    @Override
    public ProcessVersionEntity retrieveProcessVersionByCod(Integer cod) {
        return getSession().refreshByPk(ProcessVersionEntity.class, cod);
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
    protected VariableInstanceEntity newVariableInstance(ProcessInstanceEntity processInstance, String name) {
        VariableInstanceEntity variable = new VariableInstanceEntity();
        variable.setProcessInstance(processInstance);
        variable.setName(name);
        return variable;
    }


    @Override
    protected IEntityExecutionVariable newExecutionVariable(ProcessInstanceEntity instance, IEntityVariableInstance processInstanceVar,
            TaskInstanceEntity originTask, TaskInstanceEntity destinationTask, IEntityVariableType type) {
        ExecutionVariableEntity novo = new ExecutionVariableEntity();
        novo.setVariable((VariableInstanceEntity) processInstanceVar);
        novo.setProcessInstance(instance);
        novo.setOriginTask(originTask);
        novo.setDestinationTask(destinationTask);
        novo.setType((VariableTypeInstance) type);
        return novo;
    }

    @Override
    protected Class<? extends IEntityVariableType> getClassEntityVariableType() {
        return VariableTypeInstance.class;
    }

    @Override
    protected Class<ProcessInstanceEntity> getClassProcessInstance() {
        return ProcessInstanceEntity.class;
    }
    
    // -------------------------------------------------------
    // Listagens
    // -------------------------------------------------------

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods) {
        throw new UnsupportedOperationException("Método não implementado");
    }

}
