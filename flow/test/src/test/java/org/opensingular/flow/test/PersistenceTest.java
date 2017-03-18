package org.opensingular.flow.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.test.definicao.Peticao;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public class PersistenceTest extends TestFlowSupport {

    @Before
    public void setUp() {
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
