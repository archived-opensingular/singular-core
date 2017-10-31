/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.flow.test;

import org.junit.Test;
import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.FlowDefinitionEntity;
import org.opensingular.flow.persistence.entity.FlowInstanceEntity;
import org.opensingular.flow.persistence.entity.FlowRight;
import org.opensingular.flow.persistence.entity.FlowRightPK;
import org.opensingular.flow.persistence.entity.FlowVersionEntity;
import org.opensingular.flow.persistence.entity.ModuleEntity;
import org.opensingular.flow.persistence.entity.RoleDefinitionEntity;
import org.opensingular.flow.persistence.entity.RoleInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskHistoricTypeEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.flow.persistence.entity.TaskPermissionEntity;
import org.opensingular.flow.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.entity.VariableInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableTypeInstance;
import org.opensingular.flow.test.support.TestFlowSupport;

import static org.junit.Assert.assertEquals;

/**
 * Testes gerais para o setup de teste do projeto
 */
public class SetupTest extends TestFlowSupport {

    @Test
    public void checkLoadTestExecuted() {
        assertEquals(4, TaskType.values().length);
    }

    @Test
    public void checkTableAccess() {
        listAllFor(Actor.class);
        listAllFor(CategoryEntity.class);
        listAllFor(ExecutionVariableEntity.class);
        listAllFor(FlowVersionEntity.class);
        listAllFor(FlowDefinitionEntity.class);
        listAllFor(ModuleEntity.class);
        listAllFor(FlowInstanceEntity.class);
        listAllFor(FlowRight.class);
        listAllFor(FlowRightPK.class);
        listAllFor(RoleDefinitionEntity.class);
        listAllFor(RoleInstanceEntity.class);
        listAllFor(TaskVersionEntity.class);
        listAllFor(TaskDefinitionEntity.class);
        listAllFor(TaskHistoricTypeEntity.class);
        listAllFor(TaskInstanceEntity.class);
        listAllFor(TaskInstanceHistoryEntity.class);
        listAllFor(TaskPermissionEntity.class);
        listAllFor(TaskTransitionVersionEntity.class);
        listAllFor(VariableInstanceEntity.class);
        listAllFor(VariableTypeInstance.class);
    }

    private void listAllFor(Class<?> clazz) {
        sessionFactory.getCurrentSession().createCriteria(clazz).list();
    }
}
