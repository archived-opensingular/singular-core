package org.opensingular.flow.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.opensingular.flow.core.DefinitionInfo;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessDefinitionCache;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.defaults.NullTaskAccessStrategy;
import org.opensingular.flow.core.ws.BaseSingularRest;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.util.HibernateSingularFlowConfigurationBean;
import org.opensingular.lib.commons.base.SingularPropertiesImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

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
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource("singular-mssql.properties"));
    }

    @Before
    public void setUp() {
        Flow.setConf(mbpmBean, true);

        session = sessionFactory.openSession();
        mbpmBean.setSessionLocator(() -> session);
        session.beginTransaction();

        P p = new P();
        id = p.prepareStartCall().createAndStart();
    }

    @After
    public void tearDown() {
        ProcessDefinitionCache.invalidateAll();
        session.close();
    }

    @Test public void relocatesTaskUser(){
        P p = new P();
        ProcessInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        new BaseSingularRest().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1", null);
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        new BaseSingularRest().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2", 1);
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(2));
    }

    @Test public void rejectssRelocationTaskUserWithWrongVersionLock(){
        P p = new P();
        ProcessInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        new BaseSingularRest().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1", 0);
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        thrown.expectMessage("Your Task Version Number is Outdated.");
        new BaseSingularRest().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2", 0);
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));
    }


    @Test public void rejectsRelocationWithInvalidVersionNumber(){
        P p = new P();
        ProcessInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(u1);

        session.flush();
        session.evict(t.getEntityTaskInstance());

        Actor u2 = testDAO.getSomeUser(2);

        thrown.expectMessage("Your Task Version Number is Outdated.");

        t.relocateTask(u2, u2, false, "Just want to watch the world burn",
                t.getEntityTaskInstance().getVersionStamp()-1);

        session.flush();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(u1);
    }

    @Test public void acceptsRelocationWithValidVersionNumber(){
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(u1);

        Actor u2 = testDAO.getSomeUser(2);
        t.relocateTask(u2, u2, false, "Just want to watch the world burn", t.getEntityTaskInstance().getVersionStamp());

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(u2);
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

    @Test public void endAllocation(){
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isEqualTo(u1);

        t.endLastAllocation();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();
    }

    private void save(TaskInstanceEntity o) {
        session.saveOrUpdate(o);
        session.flush();
        session.evict(o);
    }

    private TaskInstanceEntity nTE(ProcessInstance id) {
        TaskInstanceEntity t = id.getCurrentTaskOrException().getEntityTaskInstance();
        TaskInstanceEntity o = new TaskInstanceEntity();
        o.setTask((TaskVersionEntity) t.getTaskVersion());
        o.setProcessInstance(t.getProcessInstance());
        o.setBeginDate(new Date());
        return o;
    }


    @DefinitionInfo("P-P")
    public static class P extends ProcessDefinition<ProcessInstance> {

        public enum PTask implements ITaskDefinition {
            START, DO1, DO2, END;

            @Override
            public String getName() {
                return this.name();
            }
        }

        public P() {
            super(ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            setName("TT", "PP");

            FlowBuilderImpl flow = new FlowBuilderImpl(this);
            flow.addJavaTask(PTask.START).call(this::doSomething);
            flow.addPeopleTask(PTask.DO1, new NullTaskAccessStrategy());

            flow.addEnd(PTask.END);
            flow.setStart(PTask.START);

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

