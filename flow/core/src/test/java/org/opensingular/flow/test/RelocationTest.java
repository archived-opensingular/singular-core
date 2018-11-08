/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.flow.test;

import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.opensingular.flow.core.*;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.defaults.PermissiveTaskAccessStrategy;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskVersionEntity;
import org.opensingular.flow.persistence.util.HibernateSingularFlowConfigurationBean;
import org.opensingular.flow.test.support.TestFlowSupport;
import org.opensingular.lib.commons.base.SingularPropertiesImpl;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@ActiveProfiles("mssql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RelocationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    protected HibernateSingularFlowConfigurationBean hibernateSingularFlowConfigurationBean;
    @Inject
    protected TestDAO testDAO;
    @Inject
    protected SessionFactory sessionFactory;
    Session session;
    private FlowInstance id;

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource("singular-mssql.properties"));
    }

    @PostConstruct
    public void init() {
        TestFlowSupport.configApplicationContext(ApplicationContextProvider.get());
    }

    @Before
    public void setUp() {
        Flow.setConf(hibernateSingularFlowConfigurationBean, true);

        session = sessionFactory.openSession();
        hibernateSingularFlowConfigurationBean.setSessionLocator(() -> session);
        session.beginTransaction();

        P p = new P();
        id = p.prepareStartCall().createAndStart();
    }

    @After
    public void tearDown() {
        FlowDefinitionCache.invalidateAll();
        session.close();
    }

    @Test
    public void relocatesTaskUser() {
        P            p  = new P();
        FlowInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        relocateTask(id, "1", null);
        Assert.assertThat(id.getCurrentTaskOrException().getAllocatedUser(), Matchers.equalTo(testDAO.getSomeUser(1)));

        relocateTask(id, "2", 1);
        Assert.assertThat((id.getCurrentTaskOrException().getAllocatedUser()), Matchers.equalTo(testDAO.getSomeUser(2)));
    }

    @Test
    public void rejectssRelocationTaskUserWithWrongVersionLock() {
        P            p  = new P();
        FlowInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        relocateTask(id,"1", 0);
        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), testDAO.getSomeUser(1));

        thrown.expectMessage("Your Task Version Number is Outdated.");
        relocateTask(id, "2", 0);
        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), testDAO.getSomeUser(1));
    }


    public void relocateTask(FlowInstance flowInstance,
                             String username,
                             Integer lastVersion) {
        SUser user = Flow.getConfigBean().getUserService().saveUserIfNeededOrException(username);
        Integer lastVersion2 = (lastVersion == null) ? Integer.valueOf(0) : lastVersion;
        flowInstance.getCurrentTaskOrException().relocateTask(user, user, false, "", lastVersion2);
    }

    @Test
    public void rejectsRelocationWithInvalidVersionNumber() {
        P            p  = new P();
        FlowInstance id = p.prepareStartCall().createAndStart();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor        u1 = testDAO.getSomeUser(1);
        TaskInstance t  = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), u1);

        session.flush();
        session.evict(t.getEntityTaskInstance());

        Actor u2 = testDAO.getSomeUser(2);

        thrown.expectMessage("Your Task Version Number is Outdated.");

        t.relocateTask(u2, u2, false, "Just want to watch the world burn",
                t.getEntityTaskInstance().getVersionStamp() - 1);

        session.flush();

        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), u1);
    }

    @Test
    public void acceptsRelocationWithValidVersionNumber() {
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor        u1 = testDAO.getSomeUser(1);
        TaskInstance t  = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), u1);

        Actor u2 = testDAO.getSomeUser(2);
        t.relocateTask(u2, u2, false, "Just want to watch the world burn", t.getEntityTaskInstance().getVersionStamp());

        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), u2);
    }

    @Test(expected = PersistenceException.class)
    public void lowLevelLockTest() {
        TaskInstanceEntity o = nTE(id);
        save(o);
        o.setBeginDate(new Date());
        save(o);
        o.setBeginDate(new Date());
        save(o);

        TaskInstanceEntity o1 = nTE(id);
        o1.setCod(o.getCod());
        o1.setVersionStamp(o.getVersionStamp() - 1);

        save(o1);
    }

    @Test
    public void endAllocation() {
        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();

        Actor        u1 = testDAO.getSomeUser(1);
        TaskInstance t  = id.getCurrentTaskOrException();
        t.relocateTask(u1, u1, false, "Just for fun");
        assertEquals(id.getCurrentTaskOrException().getAllocatedUser(), u1);

        t.endLastAllocation();

        assertThat(id.getCurrentTaskOrException().getAllocatedUser()).isNull();
    }

    private void save(TaskInstanceEntity o) {
        session.saveOrUpdate(o);
        session.flush();
        session.evict(o);
    }

    private TaskInstanceEntity nTE(FlowInstance id) {
        TaskInstanceEntity t = id.getCurrentTaskOrException().getEntityTaskInstance();
        TaskInstanceEntity o = new TaskInstanceEntity();
        o.setCurrentInstanceStatus(CurrentInstanceStatus.UNDEFINED);
        o.setTask((TaskVersionEntity) t.getTaskVersion());
        o.setFlowInstance(t.getFlowInstance());
        o.setBeginDate(new Date());
        return o;
    }


    @DefinitionInfo("P-P")
    public static class P extends FlowDefinition<FlowInstance> {

        public enum PTask implements ITaskDefinition {
            START, DO1, DO2, END;

            @Override
            public String getName() {
                return this.name();
            }
        }

        public P() {
            super(FlowInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            setName("TT", "PP");

            FlowBuilderImpl flow = new FlowBuilderImpl(this);
            flow.addJavaTask(PTask.START).call(this::doSomething);
            flow.addHumanTask(PTask.DO1, new PermissiveTaskAccessStrategy());

            flow.addEndTask(PTask.END);
            flow.setStartTask(PTask.START);

            flow.from(PTask.START).go(PTask.DO1);
//        flow.from(PTask.START).go(PTask.DO2);
            flow.from(PTask.DO1).go(PTask.END);
//        flow.from(PTask.DO2).go(PTask.END);

            return flow.build();
        }

        public Object doSomething(ExecutionContext executionContext) {
            return null;
        }

    }
}

