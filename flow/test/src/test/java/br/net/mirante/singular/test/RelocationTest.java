package br.net.mirante.singular.test;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.defaults.NullTaskAccessStrategy;
import br.net.mirante.singular.flow.core.ws.SingularWS;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskVersionEntity;
import br.net.mirante.singular.test.support.TestSupport;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by nuk on 15/04/16.
 */
@ActiveProfiles("mssql")
public class RelocationTest extends TestSupport {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-mssql.properties"));
    }

    @Before
    public void setup() {
        Flow.setConf(mbpmBean, true);
    }

    @After
    public void tearDown() {
        ProcessDefinitionCache.invalidateAll();
    }

    @Test public void relocatesTaskUser(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();

        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1");
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2");
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(2));
    }

    @Test public void relocatesTaskUser2(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();


        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        Actor u1 = testDAO.getSomeUser(1);
        TaskInstance t = id.getCurrentTask();
        t.relocateTask(u1, u1, false, "Just for fun", null);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u1);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().evict(t.getEntityTaskInstance());

        Actor u2 = testDAO.getSomeUser(2);
        t.relocateTask(u2, u2, false, "Just want to watch the world burn", t.getEntityTaskInstance().getVersionStamp()+1);
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(u2);
    }

    @Ignore
    @Test public void relocatesTaskUser3() {
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();

        TaskInstanceEntity o = nTE(id);
        save(o);
        o.setBeginDate(new Date());
        update(o);
        o.setBeginDate(new Date());
        update(o);

        TaskInstanceEntity o1 = nTE(id);
        o1.setCod(o.getCod());
        o1.setVersionStamp(o.getVersionStamp()-1);
        update(o1);

        sessionFactory.getCurrentSession().flush();
    }

    private void update(TaskInstanceEntity o) {
//        testDAO.update(o);
//        sessionFactory.getCurrentSession().evict(o);
        save(o);
    }

    private void save(TaskInstanceEntity o) {
//        testDAO.save(o);
//        sessionFactory.getCurrentSession().evict(o);
//        sessionFactory.getCurrentSession().close();
        Session s = sessionFactory.getCurrentSession();
//        Session s = sessionFactory.openSession();
//        Transaction t = s.beginTransaction();
        s.saveOrUpdate(o);
        s.flush();
        s.evict(o);
//        t.commit();
//        s.close();
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
//        flow.addJavaTask(PTask.DO2).call(this::doSomething);

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

