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

import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.FlowDefinitionEntity;
import org.opensingular.flow.persistence.entity.FlowVersionEntity;
import org.opensingular.flow.persistence.entity.RoleDefinitionEntity;
import org.opensingular.flow.persistence.entity.RoleInstanceEntity;
import org.opensingular.flow.persistence.entity.RoleTaskEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.lib.support.persistence.SessionLocator;

import java.util.ArrayList;

public class DefaultHibernateFlowDefinitionService
        extends
        AbstractHibernateFlowDefinitionService<CategoryEntity, FlowDefinitionEntity, FlowVersionEntity, TaskDefinitionEntity, TaskVersionEntity, TaskTransitionVersionEntity, RoleDefinitionEntity, RoleInstanceEntity, RoleTaskEntity> {

    public DefaultHibernateFlowDefinitionService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    @Override
    protected Class<FlowDefinitionEntity> getClassFlowDefinition() {
        return FlowDefinitionEntity.class;
    }

    @Override
    protected Class<? extends RoleDefinitionEntity> getClassRoleDefinition() {
        return RoleDefinitionEntity.class;
    }
    
    @Override
    protected Class<RoleInstanceEntity> getClassRoleInstance() {
        return RoleInstanceEntity.class;
    }

    @Override
    protected Class<CategoryEntity> getClassCategory() {
        return CategoryEntity.class;
    }

    @Override
    public FlowVersionEntity createEntityFlowVersion(FlowDefinitionEntity entityFlowDefinition) {
        FlowVersionEntity flowVersion = new FlowVersionEntity();
        flowVersion.setFlowDefinition(entityFlowDefinition);
        flowVersion.setVersionTasks(new ArrayList<>());
        return flowVersion;
    }

    @Override
    public TaskTransitionVersionEntity createEntityTaskTransition(TaskVersionEntity originTask, TaskVersionEntity destinationTask) {
        TaskTransitionVersionEntity taskEntity = new TaskTransitionVersionEntity();
        taskEntity.setOriginTask(originTask);
        taskEntity.setDestinationTask(destinationTask);
        return taskEntity;
    }

    @Override
    public TaskVersionEntity createEntityTaskVersion(FlowVersionEntity flowVersion, TaskDefinitionEntity entityTaskDefinition, STask<?> task) {
        TaskVersionEntity taskEntity = new TaskVersionEntity();
        taskEntity.setFlowVersion(flowVersion);
        taskEntity.setTaskDefinition(entityTaskDefinition);
        taskEntity.setType((TaskType) task.getEffectiveTaskType());
        taskEntity.setTransitions(new ArrayList<>());
        return taskEntity;
    }


    @Override
    protected TaskDefinitionEntity createEntityDefinitionTask(FlowDefinitionEntity flow) {
        TaskDefinitionEntity taskDefinition = new TaskDefinitionEntity();
        taskDefinition.setFlowDefinition(flow);
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
