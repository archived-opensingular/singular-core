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
import org.opensingular.flow.core.TestProcessWithFlowDecisionAsFirstStep.ProcessWithFlowDecisionAsFirstStep.StepsDE;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProcessWithFlowDecisionAsFirstStep extends TestFlowExecutionSupport {

    public static final String FLAG = "flag";

    private static StepsDE action;

    @Test
    public void goToResultA() {
        goToResult(StepsDE.ResultA);
    }

    @Test
    public void goToResultB() {
        goToResult(StepsDE.ResultB);
    }

    private void goToResult(StepsDE target) {
        action = target;
        ProcessInstance pi = new ProcessWithFlowDecisionAsFirstStep().prepareStartCall().createAndStart();
        assertions(pi).isAtTask(target);

        pi = reload(pi);
        assertions(pi).isAtTask(target);
    }

    @DefinitionInfo("FlowDecisionAsFirstStep")
    public static class ProcessWithFlowDecisionAsFirstStep extends ProcessDefinition<ProcessInstance> {

        public enum StepsDE implements ITaskDefinition {
            First, ResultA, ResultB, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public ProcessWithFlowDecisionAsFirstStep() {
            super(ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addJavaTask(StepsDE.First).call(this::decideNextTask);
            f.addWaitTask(StepsDE.ResultA);
            f.addWaitTask(StepsDE.ResultB);
            f.addEnd(StepsDE.End);

            f.setStart(StepsDE.First);
            f.from(StepsDE.First).go(StepsDE.ResultA).thenGo(StepsDE.End);
            f.from(StepsDE.First).go(StepsDE.ResultB).thenGo(StepsDE.End);

            return f.build();
        }

        void decideNextTask(ProcessInstance processInstance, ExecutionContext execucaoTask) {
            if (action != null) {
                execucaoTask.setTransition(action.getKey());
            }
        }
    }
}

