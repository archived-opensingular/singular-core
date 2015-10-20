package br.net.mirante.singular.test;

import br.net.mirante.singular.definicao.Peticao;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.test.support.TestSupport;
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
        br.net.mirante.singular.persistence.entity.ProcessInstanceEntity pientity = (br.net.mirante.singular.persistence.entity.ProcessInstanceEntity) sessionFactory.getCurrentSession().load(br.net.mirante.singular.persistence.entity.ProcessInstanceEntity.class, cod);
        Assert.assertNotNull(pientity.getCurrentTask());
        Assert.assertEquals(pientity.getCurrentTask().getClass(), TaskInstanceEntity.class);
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD END. ");
    }
}
