package br.net.mirante.singular.test;

import br.net.mirante.singular.InstanciaDefinicao;
import br.net.mirante.singular.TestDAO;
import br.net.mirante.singular.TestMBPMBean;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.persistence.entity.*;
import br.net.mirante.singular.persistence.entity.Process;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class DefinitionTest {

    @Inject
    private TestMBPMBean mbpmBean;

    @Inject
    private TestDAO testDAO;

    @Inject
    private SessionFactory sessionFactory;

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
    @Transactional
    public void veriifcarAcessoEmTodasAsTabelas() {
        sessionFactory.getCurrentSession().createCriteria(Actor.class).list();
        sessionFactory.getCurrentSession().createCriteria(Category.class).list();
        sessionFactory.getCurrentSession().createCriteria(ExecutionVariable.class).list();
        sessionFactory.getCurrentSession().createCriteria(Process.class).list();
        sessionFactory.getCurrentSession().createCriteria(ProcessDefinition.class).list();
        sessionFactory.getCurrentSession().createCriteria(ProcessInstance.class).list();
        sessionFactory.getCurrentSession().createCriteria(ProcessRight.class).list();
        sessionFactory.getCurrentSession().createCriteria(ProcessRightPK.class).list();
        sessionFactory.getCurrentSession().createCriteria(Role.class).list();
        sessionFactory.getCurrentSession().createCriteria(RoleInstance.class).list();
        sessionFactory.getCurrentSession().createCriteria(Task.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskDefinition.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskHistoryType.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskInstance.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskInstanceHistory.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskRight.class).list();
        sessionFactory.getCurrentSession().createCriteria(TaskType.class).list();
        sessionFactory.getCurrentSession().createCriteria(Transition.class).list();
        sessionFactory.getCurrentSession().createCriteria(Variable.class).list();
        sessionFactory.getCurrentSession().createCriteria(VariableType.class).list();
    }

    @Test
    public void testeInserir() {
        TaskType tt = new TaskType();
        tt.setAbbreviation("teste");

        testDAO.save(tt);

    }

}
