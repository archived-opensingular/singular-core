package br.net.mirante.singular.test;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.defaults.NullTaskAccessStrategy;
import br.net.mirante.singular.flow.core.ws.SingularWS;
import br.net.mirante.singular.flow.test.TestDAO;
import br.net.mirante.singular.persistence.entity.*;
import br.net.mirante.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by nuk on 15/04/16.
 */
@ActiveProfiles("mssql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RelocationTest  {

    @Inject
    protected HibernateSingularFlowConfigurationBean mbpmBean;

    @Inject
    protected TestDAO testDAO;

    @Inject
    protected SessionFactory sessionFactory;

    @Rule public ExpectedException thrown = ExpectedException.none();
    private ProcessInstance id;

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-mssql.properties"));
    }

    @Before
    public void setup() {
        Flow.setConf(mbpmBean, true);

        session = sessionFactory.openSession();
        mbpmBean.setSessionLocator(() -> session);
        session.beginTransaction();

        P p = new P();
        id = p.newInstance();
        id.start();
    }

    @After
    public void tearDown() {
        ProcessDefinitionCache.invalidateAll();
        session.close();
    }

    @Test public void relocatesTaskUser(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();

        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1", null);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2", 1);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(2));
    }

    @Test public void rejectssRelocationTaskUserWithWrongVersionLock(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();

        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1", 0);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        thrown.expectMessage("Your Task Version Number is Outdated.");
        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2", 0);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));
    }


    @Test public void rejectsRelocationWithInvalidVersionNumber(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();

        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTask();
        t.relocateTask(u1, u1, false, "Just for fun", null);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u1);

        session.flush();
        session.evict(t.getEntityTaskInstance());

        Actor u2 = testDAO.getSomeUser(2);

        thrown.expectMessage("Your Task Version Number is Outdated.");

        t.relocateTask(u2, u2, false, "Just want to watch the world burn",
                t.getEntityTaskInstance().getVersionStamp()-1);

        session.flush();

        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u1);
    }

    @Test public void acceptsRelocationWithValidVersionNumber(){
        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTask();
        t.relocateTask(u1, u1, false, "Just for fun", null);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u1);

        Actor u2 = testDAO.getSomeUser(2);
        t.relocateTask(u2, u2, false, "Just want to watch the world burn", t.getEntityTaskInstance().getVersionStamp());

        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u2);
    }

    Session session;

    @Test(expected = StaleObjectStateException.class) public void lowLevelLockTest() {
        TaskInstanceEntity o = nTE(id);
        save(o);
        o.setBeginDate(new Date());
        save(o);
        o.setBeginDate(new Date());
        save(o);

        TaskInstanceEntity o1 = nTE(id);
        o1.setCod(o.getCod());
        o1.setVersionStamp(o.getVersionStamp()-1);

        save(o1);
    }

    private void save(TaskInstanceEntity o) {
        session.saveOrUpdate(o);
        session.flush();
        session.evict(o);
    }

    private TaskInstanceEntity nTE(ProcessInstance id) {
        TaskInstanceEntity t = id.getCurrentTask().getEntityTaskInstance();
        TaskInstanceEntity o = new TaskInstanceEntity();
        o.setTask((TaskVersionEntity) t.getTask());
        o.setProcessInstance(t.getProcessInstance());
        o.setBeginDate(new Date());
        return o;
    }


    public static class P extends ProcessDefinition<ProcessInstance> {

        public enum PTask implements ITaskDefinition {
            START, DO1, DO2, END;

            @Override
            public String getName() {
                return this.name();
            }
        }

        public P() {
            super("P-P",ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            setName("TT", "PP");

            FlowBuilderImpl flow = new FlowBuilderImpl(this);
            flow.addJavaTask(PTask.START).call(this::doSomething);
            flow.addPeopleTask(PTask.DO1, new NullTaskAccessStrategy());

            flow.addEnd(PTask.END);
            flow.setStartTask(PTask.START);

            flow.from(PTask.START).go(PTask.DO1);
//        flow.from(PTask.START).go(PTask.DO2);
            flow.from(PTask.DO1).go(PTask.END);
//        flow.from(PTask.DO2).go(PTask.END);

            return flow.build();
        }

        public void doSomething(ProcessInstance instancia, ExecutionContext ctxExecucao) {

        }

    }
}

