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
import org.opensingular.flow.core.TestFlowWithFlowDecisionAsFirstStep.FlowWithFlowDecisionAsFirstStep.StepsDE;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFlowWithFlowDecisionAsFirstStep extends TestFlowExecutionSupport {

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

    @Test
    public void goToWrongTransition() {
        SingularTestUtil.assertException(() -> goToResult(StepsDE.End), SingularFlowTransactionNotFoundException.class);
    }

    @Test
    public void goToUndefinedTransition() {
        SingularTestUtil.assertException(() -> goToResult(null), SingularFlowTransactionNotFoundException.class);
    }

    private void goToResult(StepsDE target) {
        action = target;
        FlowInstance pi = new FlowWithFlowDecisionAsFirstStep().prepareStartCall().createAndStart();
        assertReloadAssert(pi, p -> assertions(p).isAtTask(target));
    }

    @DefinitionInfo("FlowDecisionAsFirstStep")
    public static class FlowWithFlowDecisionAsFirstStep extends FlowDefinition<FlowInstance> {

        public enum StepsDE implements ITaskDefinition {
            First, ResultA, ResultB, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public FlowWithFlowDecisionAsFirstStep() {
            super(FlowInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addJavaTask(StepsDE.First).call(this::decideNextTask);
            f.addWaitTask(StepsDE.ResultA);
            f.addWaitTask(StepsDE.ResultB);
            f.addEndTask(StepsDE.End);

            f.setStartTask(StepsDE.First);
            f.from(StepsDE.First).go(StepsDE.ResultA).thenGo(StepsDE.End);
            f.from(StepsDE.First).go(StepsDE.ResultB).thenGo(StepsDE.End);

            return f.build();
        }

        Object decideNextTask(ExecutionContext executionContext) {
            if (action != null) {
                executionContext.setTransition(action.getKey());
            }
            return null;
        }
    }
}

