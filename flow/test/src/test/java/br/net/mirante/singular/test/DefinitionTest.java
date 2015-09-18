package br.net.mirante.singular.test;

import br.net.mirante.singular.CoisasQueDeviamSerParametrizadas;
import br.net.mirante.singular.Definicao;
import br.net.mirante.singular.InstanciaDefinicao;
import br.net.mirante.singular.InstanciaDefinicaoComVariavel;
import br.net.mirante.singular.TestDAO;
import br.net.mirante.singular.TestMBPMBean;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.Process;
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
@Transactional
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

    @Test
    public void teste() {

        InstanciaDefinicao id = iniciarFluxo();


        InstanciaDefinicao id2 = MBPM.findProcessInstance(id.getFullId());
        assertNotNull(id2);
        System.out.println("legal");
    }

    private InstanciaDefinicao iniciarFluxo() {
        InstanciaDefinicao id = new InstanciaDefinicao();
        id.start();
        return id;
    }
    
    /**
     * Esse teste falha não sei exatamente por qual motivo mas é devido ao hsqldb não encontrar um
     * dado que já inseriu em um teste anterior, nesse caso a persistencia da definicao do processo
     */
    @Test
    public void teste2(){
        teste();
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
    @Transactional
    public void testarUsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();
        if (id2.isEnd()) {
            System.out.println("acabou");
        }
    }


    @Test
    public void testeInserir() {
        TaskType tt = new TaskType();
        tt.setAbbreviation("teste");

        testDAO.save(tt);

    }

    @Test
    public void testarDefinicao() {
        InstanciaDefinicao instanciaDefinicao = iniciarFluxo();
        instanciaDefinicao.executeTransition();
    }

}
