/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.persistence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityTaskHistoricType;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.flow.persistence.entity.ProcessVersionEntity;
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
    protected VariableInstanceEntity retrieveVariableInstanceByCod(Integer cod) {
        return getSession().retrieve(VariableInstanceEntity.class, cod);
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
