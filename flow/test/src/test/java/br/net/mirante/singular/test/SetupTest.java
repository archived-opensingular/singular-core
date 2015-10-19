package br.net.mirante.singular.test;

import java.util.List;

import org.junit.Test;

import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.CategoryEntity;
import br.net.mirante.singular.persistence.entity.ExecutionVariableEntity;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.ProcessRight;
import br.net.mirante.singular.persistence.entity.ProcessRightPK;
import br.net.mirante.singular.persistence.entity.RoleDefinitionEntity;
import br.net.mirante.singular.persistence.entity.RoleInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskVersionEntity;
import br.net.mirante.singular.persistence.entity.TaskDefinitionEntity;
import br.net.mirante.singular.persistence.entity.TaskHistoricTypeEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistoryEntity;
import br.net.mirante.singular.persistence.entity.TaskRight;
import br.net.mirante.singular.persistence.entity.TaskTypeEntity;
import br.net.mirante.singular.persistence.entity.TaskTransitionVersionEntity;
import br.net.mirante.singular.persistence.entity.VariableInstanceEntity;
import br.net.mirante.singular.persistence.entity.VariableTypeInstance;

import static org.junit.Assert.assertEquals;

/**
 * Testes gerais para o setup de teste do projeto
 */
public class SetupTest extends TestSupport {

    @Test
    public void checkLoadTestExecuted() {
        List<TaskTypeEntity> taskTypes = testDAO.listTaskType();
        assertEquals(4, taskTypes.size());
    }

    @Test
    public void checkTableAccess() {
        listaAllFor(Actor.class);
        listaAllFor(CategoryEntity.class);
        listaAllFor(ExecutionVariableEntity.class);
        listaAllFor(br.net.mirante.singular.persistence.entity.ProcessVersionEntity.class);
        listaAllFor(ProcessDefinitionEntity.class);
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
        listaAllFor(TaskRight.class);
        listaAllFor(TaskTypeEntity.class);
        listaAllFor(TaskTransitionVersionEntity.class);
        listaAllFor(VariableInstanceEntity.class);
        listaAllFor(VariableTypeInstance.class);
    }

    private void listaAllFor(Class<?> clazz) {
        sessionFactory.getCurrentSession().createCriteria(clazz).list();
    }
}
