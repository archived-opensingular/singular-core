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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.TestProcessInicializationWithoutParameters.ProcessWithInitialization.Steps;
import org.opensingular.flow.core.builder.BTransition;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.variable.VarDefinitionMap;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProcessInicializationWithoutParameters extends TestFlowExecutionSupport {

    public static final String FLAG = "flag";

    private static boolean startInitializerCalled;

    @Test
    public void simpleStart() {
        startInitializerCalled = false;
        ProcessInstance pi = new ProcessWithInitialization().prepareStartCall().createAndStart();

        assertTrue(startInitializerCalled);
        assertions(pi).isAtTask(Steps.Second).isVariableValue(FLAG, 11);

        pi = reload(pi);
        assertions(pi).isAtTask(Steps.Second).isVariableValue(FLAG, 11);
    }

    @DefinitionInfo("WithoutParameters")
    public static class ProcessWithInitialization extends ProcessDefinition<ProcessInstance> {

        public enum Steps implements ITaskDefinition {
            First, Second, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public ProcessWithInitialization() {
            super(ProcessInstance.class);
            getVariables().addVariableInteger(FLAG);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addJavaTask(Steps.First).call(this::doFirst);
            f.addWaitTask(Steps.Second);
            f.addEnd(Steps.End);

            f.setStart(Steps.First).withInitializer(this::processInitializer);
            f.from(Steps.First).go(Steps.Second).thenGo(Steps.End);

            return f.build();
        }

        private void processInitializer(ProcessInstance instance, StartCall<ProcessInstance> startCall) {
            startInitializerCalled = true;
            instance.getVariables().setValue(FLAG, 1);
            instance.start();
        }


        private void configFastTransition(BTransition<?> fastTransition) {
            VarDefinitionMap<?> parameters = fastTransition.getTransition().getParameters();
            parameters.addVariableString("motivo").required();
            parameters.addVariableString("extraInfo)");
        }

        public void doFirst(TaskInstance task) {
            Integer v = task.getProcessInstance().getVariables().getValueInteger(FLAG, 0);
            task.getProcessInstance().getVariables().setValue(FLAG, v + 10);
        }
    }
}

