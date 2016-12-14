package org.opensingular.flow.test;

import static org.junit.Assert.assertEquals;

import org.opensingular.flow.persistence.entity.ProcessVersionEntity;
import org.opensingular.flow.persistence.entity.TaskPermissionEntity;
import org.junit.Test;

import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.CategoryEntity;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.flow.persistence.entity.ProcessRight;
import org.opensingular.flow.persistence.entity.ProcessRightPK;
import org.opensingular.flow.persistence.entity.RoleDefinitionEntity;
import org.opensingular.flow.persistence.entity.RoleInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskHistoricTypeEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.flow.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.entity.VariableInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableTypeInstance;
import org.opensingular.flow.test.support.TestSupport;

/**
 * Testes gerais para o setup de teste do projeto
 */
public abstract class SetupTest extends TestSupport {

    @Test
    public void checkLoadTestExecuted() {
        assertEquals(4, TaskType.values().length);
    }

    @Test
    public void checkTableAccess() {
        listaAllFor(Actor.class);
        listaAllFor(CategoryEntity.class);
        listaAllFor(ExecutionVariableEntity.class);
        listaAllFor(ProcessVersionEntity.class);
        listaAllFor(ProcessDefinitionEntity.class);
        listaAllFor(ProcessGroupEntity.class);
        listaAllFor(ProcessInstanceEntity.class);
        listaAllFor(ProcessRight.class);
        listaAllFor(ProcessRightPK.class);
        listaAllFor(RoleDefinitionEntity.class);
        listaAllFor(RoleInstanceEntity.class);
        listaAllFor(TaskVersionEntity.class);
        listaAllFor(TaskDefinitionEntity.class);
        listaAllFor(TaskHistoricTypeEntity.class);
        listaAllFor(TaskInstanceEntity.class);
        listaAllFor(TaskInstanceHistoryEntity.class);
        listaAllFor(TaskPermissionEntity.class);
        listaAllFor(TaskTransitionVersionEntity.class);
        listaAllFor(VariableInstanceEntity.class);
        listaAllFor(VariableTypeInstance.class);
    }

    private void listaAllFor(Class<?> clazz) {
        sessionFactory.getCurrentSession().createCriteria(clazz).list();
    }
}
