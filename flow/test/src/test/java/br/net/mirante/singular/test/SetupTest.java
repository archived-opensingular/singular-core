package br.net.mirante.singular.test;

import java.util.List;

import org.junit.Test;

import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.ProcessRight;
import br.net.mirante.singular.persistence.entity.ProcessRightPK;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.TaskRight;
import br.net.mirante.singular.persistence.entity.TaskType;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;

import static org.junit.Assert.assertEquals;

/**
 * Testes gerais para o setup de teste do projeto
 */
public class SetupTest extends TestSupport {

    @Test
    public void checkLoadTestExecuted() {
        List<TaskType> taskTypes = testDAO.listTaskType();
        assertEquals(4, taskTypes.size());
    }

    @Test
    public void checkTableAccess() {
        listaAllFor(Actor.class);
        listaAllFor(Category.class);
        listaAllFor(ExecutionVariable.class);
        listaAllFor(br.net.mirante.singular.persistence.entity.Process.class);
        listaAllFor(ProcessDefinition.class);
        listaAllFor(ProcessInstance.class);
        listaAllFor(ProcessRight.class);
        listaAllFor(ProcessRightPK.class);
        listaAllFor(Role.class);
        listaAllFor(RoleInstance.class);
        listaAllFor(Task.class);
        listaAllFor(TaskDefinition.class);
        listaAllFor(TaskHistoryType.class);
        listaAllFor(TaskInstance.class);
        listaAllFor(TaskInstanceHistory.class);
        listaAllFor(TaskRight.class);
        listaAllFor(TaskType.class);
        listaAllFor(Transition.class);
        listaAllFor(Variable.class);
        listaAllFor(VariableType.class);
    }

    private void listaAllFor(Class<?> clazz) {
        sessionFactory.getCurrentSession().createCriteria(clazz).list();
    }
}
