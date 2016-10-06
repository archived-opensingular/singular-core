package org.opensingular.flow.test;

import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.flow.test.definicao.Peticao;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.test.support.TestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public abstract class PersistenceTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void testJoinTableCurrentTask() {
        ProcessInstance pi = new Peticao().newInstance();
        pi.start();
        Integer cod = pi.getEntity().getCod();
        sessionFactory.getCurrentSession().flush();
        //Clear da sessão para evidenciar a consulta como única.
        sessionFactory.getCurrentSession().clear();
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD BEGIN: Clear na sessão, recarregando process instance: ");
        ProcessInstanceEntity pientity = (ProcessInstanceEntity) sessionFactory.getCurrentSession().load(ProcessInstanceEntity.class, cod);
        Assert.assertNotNull(pientity.getCurrentTask());
        Assert.assertEquals(pientity.getCurrentTask().getClass(), TaskInstanceEntity.class);
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD END. ");
    }
}
