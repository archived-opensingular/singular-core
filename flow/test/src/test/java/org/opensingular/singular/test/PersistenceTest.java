package org.opensingular.singular.test;

import org.opensingular.singular.flow.test.definicao.Peticao;
import org.opensingular.singular.flow.core.Flow;
import org.opensingular.singular.flow.core.ProcessInstance;
import org.opensingular.singular.persistence.entity.TaskInstanceEntity;
import org.opensingular.singular.test.support.TestSupport;
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
        org.opensingular.singular.persistence.entity.ProcessInstanceEntity pientity = (org.opensingular.singular.persistence.entity.ProcessInstanceEntity) sessionFactory.getCurrentSession().load(org.opensingular.singular.persistence.entity.ProcessInstanceEntity.class, cod);
        Assert.assertNotNull(pientity.getCurrentTask());
        Assert.assertEquals(pientity.getCurrentTask().getClass(), TaskInstanceEntity.class);
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD END. ");
    }
}
