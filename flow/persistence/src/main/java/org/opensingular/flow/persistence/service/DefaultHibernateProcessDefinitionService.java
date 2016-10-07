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

import java.util.ArrayList;

import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.ProcessVersionEntity;
import org.opensingular.flow.persistence.entity.RoleDefinitionEntity;
import org.opensingular.flow.persistence.entity.RoleInstanceEntity;
import org.opensingular.flow.persistence.entity.RoleTaskEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.entity.util.SessionLocator;

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
