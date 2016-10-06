package org.opensingular.singular.test;

import static org.junit.Assert.assertEquals;

import org.opensingular.singular.persistence.entity.TaskPermissionEntity;
import org.junit.Test;

import org.opensingular.singular.flow.core.TaskType;
import org.opensingular.singular.persistence.entity.Actor;
import org.opensingular.singular.persistence.entity.CategoryEntity;
import org.opensingular.singular.persistence.entity.ExecutionVariableEntity;
import org.opensingular.singular.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.singular.persistence.entity.ProcessGroupEntity;
import org.opensingular.singular.persistence.entity.ProcessInstanceEntity;
import org.opensingular.singular.persistence.entity.ProcessRight;
import org.opensingular.singular.persistence.entity.ProcessRightPK;
import org.opensingular.singular.persistence.entity.RoleDefinitionEntity;
import org.opensingular.singular.persistence.entity.RoleInstanceEntity;
import org.opensingular.singular.persistence.entity.TaskDefinitionEntity;
import org.opensingular.singular.persistence.entity.TaskHistoricTypeEntity;
import org.opensingular.singular.persistence.entity.TaskInstanceEntity;
import org.opensingular.singular.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.singular.persistence.entity.TaskTransitionVersionEntity;
import org.opensingular.singular.persistence.entity.TaskVersionEntity;
import org.opensingular.singular.persistence.entity.VariableInstanceEntity;
import org.opensingular.singular.persistence.entity.VariableTypeInstance;
import org.opensingular.singular.test.support.TestSupport;

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
        listaAllFor(org.opensingular.singular.persistence.entity.ProcessVersionEntity.class);
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
