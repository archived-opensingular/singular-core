package br.net.mirante.singular.test;

import br.net.mirante.singular.Definicao;
import br.net.mirante.singular.InstanciaDefinicao;
import br.net.mirante.singular.TestDAO;
import br.net.mirante.singular.TestMBPMBean;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.persistence.entity.TaskType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DefinitionTest {

    @Inject
    private TestMBPMBean mbpmBean;

    @Inject
    private TestDAO testDAO;

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);

    }

    @Transactional
    @Test
    public void teste() {

        InstanciaDefinicao id = new InstanciaDefinicao();
        id.start();

        InstanciaDefinicao id2 = MBPM.findProcessInstance(id.getFullId());
        assertNotNull(id2);
        System.out.println("legal");
    }

    @Test
    public void loadTabelas() {
        List<TaskType> taskTypes = testDAO.listTaskType();
        assertEquals(4, taskTypes.size());
    }

    @Test
    public void testeInserir() {
        TaskType tt = new TaskType();
        tt.setAbbreviation("teste");

        testDAO.save(tt);

    }

}
