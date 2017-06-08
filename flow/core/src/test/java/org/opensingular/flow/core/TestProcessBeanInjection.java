/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.TestProcessBeanInjection.ProcessDefinitionBeanInjection.StepsBI;
import org.opensingular.flow.core.builder.BuilderJava;
import org.opensingular.flow.core.builder.BuilderStart;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;
import org.opensingular.lib.commons.net.WebRef;

import javax.inject.Inject;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel C. Bordin on 18/05/2017.
 */
public class TestProcessBeanInjection extends TestFlowExecutionSupport {

    enum IPoint {
        processDefinition, callStartListener, call1, call2, call3, call4, call5, call6, call7, call8, callBasic
    }

    private static final Set<IPoint> executedPoints = EnumSet.noneOf(IPoint.class);

    @Before
    public void clean() {
        executedPoints.clear();
    }

    @Test
    public void injectionOnProcessDefinition() {
        ProcessDefinitionBeanInjection pd = new ProcessDefinitionBeanInjection();
        assertBean(pd.myBean);

        STaskPeople taskPeople = (STaskPeople) pd.getFlowMap().getTask(StepsBI.Third);
        assertBean(((MyPageStrategy) taskPeople.getExecutionPage()).myBean);
        assertBean(((MyAccessStrategy) taskPeople.getAccessStrategy()).myBean);
    }

    @Test
    public void injectionOnCallTask() {
        runAndAssert(IPoint.callBasic, IPoint.callStartListener, IPoint.call1, IPoint.call2, IPoint.call3, IPoint.call4,
                IPoint.call5, IPoint.call6, IPoint.call7, IPoint.call8);
    }


    @Test
    public void injectionIntoProcessInstance() {
        ProcessDefinitionBeanInjection p = new ProcessDefinitionBeanInjection();
        ProcessInstanceBeanInjection i = p.prepareStartCall().createAndStart();
        assertBean(i.myBean);

        ProcessInstanceBeanInjection i2 = (ProcessInstanceBeanInjection) reload(i);
        assertBean(i2.myBean);

        ProcessInstanceBeanInjection i3 = SingularIOUtils.serializeAndDeserialize(i, true);
        assertBean(i3.myBean);
    }


    private void runAndAssert(IPoint... expectedPoints) {
        ProcessDefinitionBeanInjection p = new ProcessDefinitionBeanInjection();
        ProcessInstance i = p.prepareStartCall().createAndStart();
        for (IPoint point : expectedPoints) {
            if (!executedPoints.contains(point)) {
                throw new AssertionError("Não foi executado o ponto de verifição de injeção: " + point);
            }
        }
    }

    static void assertBean(MyBean current, IPoint point) {
        assertBean(current);
        executedPoints.add(point);
    }

    static void assertBean(MyBean current) {
        Assert.assertNotNull(current);
        assertEquals(myBeanRef.getV(), current.getV());
    }

    @DefinitionInfo("ProcessoBeanInjection")
    public static class ProcessDefinitionBeanInjection extends ProcessDefinition<ProcessInstanceBeanInjection> {

        public enum StepsBI implements ITaskDefinition {
            First, Second, Third, Call1, Call2, Call3, Call4, Call5, Call6, Call7, Call8, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public ProcessDefinitionBeanInjection() {
            super(ProcessInstanceBeanInjection.class);
        }

        @Inject
        private MyBean myBean;

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addJavaTask(StepsBI.First).call(new MyJavaTask()).addStartedTaskListener(new MyStartListener());
            f.addWaitTask(StepsBI.Second);
            f.addPeopleTask(StepsBI.Third).withExecutionPage(new MyPageStrategy()).addAccessStrategy(
                    new MyAccessStrategy());
            f.addJavaTask(StepsBI.Call1).call(new MyJavaTask1());
            f.addJavaTask(StepsBI.Call2).call(new MyJavaTask2());
            f.addJavaTask(StepsBI.Call3).call(new MyJavaTask3());
            f.addJavaTask(StepsBI.Call4).call(new MyJavaTask4());
            f.addJavaTask(StepsBI.Call5).call(new MyJavaTask5());
            f.addJavaTask(StepsBI.Call6).call(new MyJavaTask6());
            f.addJavaTask(StepsBI.Call7).call(new MyJavaTask7());
            f.addJavaTask(StepsBI.Call8).call(new MyJavaTask8());
            f.addEnd(StepsBI.End);

            f.setStart(StepsBI.First).with(this::setupStartParameters);

            f.from(StepsBI.First).go(StepsBI.Call1).thenGo(StepsBI.Call2).thenGo(StepsBI.Call3).thenGo(StepsBI.Call4)
                    .thenGo(StepsBI.Call5).thenGo(StepsBI.Call6).thenGo(StepsBI.Call7).thenGo(StepsBI.Call8);
            f.from(StepsBI.Call8).go(StepsBI.Second).setAsDefaultTransiton();

            f.from(StepsBI.Second).go(StepsBI.Third).thenGo(StepsBI.End);

            return f.build();
        }

        private <K extends ProcessInstance> void validateParamTransition2(VarInstanceMap<?, ?> vars,
                ValidationResult result, K process) {
        }

        private void setupStartParameters(BuilderStart<?> start) {
        }
    }

    public static class ProcessInstanceBeanInjection extends ProcessInstance {

        @Inject
        private MyBean myBean;

    }

    private static class MyJavaTask implements STaskJava.ImplTaskJava {

        @Inject
        public MyBean myBean;

        @Override
        public Object call(ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.callBasic);
            return null;
        }
    }

    private static class MyJavaTask1 implements BuilderJava.ImplTaskJavaReturnInstanciaExecucao<ProcessInstance> {

        @Inject
        public MyBean myBean;

        @Override
        public Object executar(ProcessInstance processInstance, ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.call1);
            return null;
        }
    }

    private static class MyJavaTask2 implements BuilderJava.ImplTaskJavaVoidInstanciaExecucao<ProcessInstance> {

        @Inject
        public MyBean myBean;

        @Override
        public void executar(ProcessInstance processInstance, ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.call2);
        }
    }

    private static class MyJavaTask3 implements BuilderJava.ImplTaskJavaVoidInstanciaTarefaExecucao {
        @Inject
        public MyBean myBean;

        @Override
        public void executar(TaskInstance taskInstance, ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.call3);
        }
    }

    private static class MyJavaTask4 implements BuilderJava.ImplTaskJavaReturnInstanciaTarefaExecucao {
        @Inject
        public MyBean myBean;

        @Override
        public Object executar(TaskInstance taskInstance, ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.call4);
            return null;
        }
    }

    private static class MyJavaTask5 implements BuilderJava.ImplTaskJavaVoidInstancia<ProcessInstance> {
        @Inject
        public MyBean myBean;

        @Override
        public void executar(ProcessInstance processInstance) {
            assertBean(myBean, IPoint.call5);
        }
    }

    private static class MyJavaTask6 implements BuilderJava.ImplTaskJavaReturnInstancia<ProcessInstance> {
        @Inject
        public MyBean myBean;

        @Override
        public Object executar(ProcessInstance processInstance) {
            assertBean(myBean, IPoint.call6);
            return null;
        }
    }

    private static class MyJavaTask7 implements BuilderJava.ImplTaskJavaVoidInstanciaTarefa {
        @Inject
        public MyBean myBean;

        @Override
        public void executar(TaskInstance taskInstance) {
            assertBean(myBean, IPoint.call7);
        }
    }

    private static class MyJavaTask8 implements BuilderJava.ImplTaskJavaReturnInstanciaTarefa {
        @Inject
        public MyBean myBean;

        @Override
        public Object executar(TaskInstance taskInstance) {
            assertBean(myBean, IPoint.call8);
            return null;
        }
    }


    private static class MyStartListener implements StartedTaskListener {

        @Inject
        public MyBean myBean;

        @Override
        public void onTaskStart(TaskInstance taskInstance, ExecutionContext execucaoTask) {
            assertBean(myBean, IPoint.callStartListener);
        }
    }

    private static class MyAccessStrategy extends TaskAccessStrategy {

        @Inject
        public MyBean myBean;

        @Override
        public boolean canExecute(ProcessInstance instance, SUser user) {
            return false;
        }

        @Override
        public Set<Integer> getFirstLevelUsersCodWithAccess(ProcessInstance instancia) {
            return null;
        }

        @Override
        public List<? extends SUser> listAllocableUsers(ProcessInstance instancia) {
            return null;
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition definicao, STask task) {
            return Collections.emptyList();
        }
    }

    private static class MyPageStrategy implements ITaskPageStrategy {

        @Inject
        public MyBean myBean;

        @Override
        public WebRef getPageFor(TaskInstance taskInstance, SUser user) {
            return null;
        }
    }
}
