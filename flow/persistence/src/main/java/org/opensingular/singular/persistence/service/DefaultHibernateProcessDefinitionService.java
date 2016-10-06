/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.service;

import java.util.ArrayList;

import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.TaskType;
import org.opensingular.singular.persistence.entity.CategoryEntity;
import org.opensingular.singular.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.singular.persistence.entity.ProcessVersionEntity;
import org.opensingular.singular.persistence.entity.RoleDefinitionEntity;
import org.opensingular.singular.persistence.entity.RoleInstanceEntity;
import org.opensingular.singular.persistence.entity.RoleTaskEntity;
import org.opensingular.singular.persistence.entity.TaskDefinitionEntity;
import org.opensingular.singular.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.singular.persistence.entity.TaskVersionEntity;
import org.opensingular.singular.persistence.entity.util.SessionLocator;

public class DefaultHibernateProcessDefinitionService
        extends AbstractHibernateProcessDefinitionService<CategoryEntity, ProcessDefinitionEntity, ProcessVersionEntity, TaskDefinitionEntity, TaskVersionEntity, TaskTransitionVersionEntity, RoleDefinitionEntity, RoleInstanceEntity, RoleTaskEntity> {

    public DefaultHibernateProcessDefinitionService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    @Override
    protected Class<ProcessDefinitionEntity> getClassProcessDefinition() {
        return ProcessDefinitionEntity.class;
    }

    @Override
    protected Class<? extends RoleDefinitionEntity> getClassProcessRoleDef() {
        return RoleDefinitionEntity.class;
    }
    
    @Override
    protected Class<RoleInstanceEntity> getClassProcessRole() {
        return RoleInstanceEntity.class;
    }

    @Override
    protected Class<CategoryEntity> getClassCategory() {
        return CategoryEntity.class;
    }

    @Override
    public ProcessVersionEntity createEntityProcessVersion(ProcessDefinitionEntity entityProcessDefinition) {
        ProcessVersionEntity entityProcess = new ProcessVersionEntity();
        entityProcess.setProcessDefinition(entityProcessDefinition);
        entityProcess.setVersionTasks(new ArrayList<>());
        return entityProcess;
    }

    @Override
    public TaskTransitionVersionEntity createEntityTaskTransition(TaskVersionEntity originTask, TaskVersionEntity destinationTask) {
        TaskTransitionVersionEntity taskEntity = new TaskTransitionVersionEntity();
        taskEntity.setOriginTask(originTask);
        taskEntity.setDestinationTask(destinationTask);
        return taskEntity;
    }

    @Override
    public TaskVersionEntity createEntityTaskVersion(ProcessVersionEntity process, TaskDefinitionEntity entityTaskDefinition, MTask<?> task) {
        TaskVersionEntity taskEntity = new TaskVersionEntity();
        taskEntity.setProcessVersion(process);
        taskEntity.setTaskDefinition(entityTaskDefinition);
        taskEntity.setType((TaskType) task.getEffectiveTaskType());
        taskEntity.setTransitions(new ArrayList<>());
        return taskEntity;
    }


    @Override
    protected TaskDefinitionEntity createEntityDefinitionTask(ProcessDefinitionEntity process) {
        TaskDefinitionEntity taskDefinition = new TaskDefinitionEntity();
        taskDefinition.setProcessDefinition(process);
        return taskDefinition;
    }

    @Override
    protected RoleTaskEntity addRoleToTask(RoleDefinitionEntity roleDefinition, TaskDefinitionEntity taskDefinition) {
        RoleTaskEntity roleTask = new RoleTaskEntity();
        roleTask.setRoleDefinition(roleDefinition);
        roleTask.setTaskDefinition(taskDefinition);
        if (taskDefinition.getRolesTask() == null) {
            taskDefinition.setRolesTask(new ArrayList<>());
        }
        taskDefinition.getRolesTask().add(roleTask);
        return roleTask;
    }
}
