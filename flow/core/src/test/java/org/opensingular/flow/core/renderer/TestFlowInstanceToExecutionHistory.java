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

package org.opensingular.flow.core.renderer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.DefinitionInfo;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.TestFlowExecutionSupport;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.renderer.TestFlowInstanceToExecutionHistory.FlowForHistory.StepsFH;
import org.opensingular.flow.test.support.TestFlowSupport;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Daniel C. Bordin
 * @since 18/05/2018
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFlowInstanceToExecutionHistory extends TestFlowExecutionSupport {

    public static final String FLAG = "flag";

    private static StepsFH action;

    @Test
    public void goToResultA() {
        FlowInstance fi = goToResult(StepsFH.ResultA);
        ExecutionHistoryForRendering history = ExecutionHistoryForRendering.from(fi, false);

        history.assertTransitionMarked(1);
        SingularTestUtil.assertException(() -> history.assertTransitionMarked(2), AssertionError.class,
                "The number of transactions marked as executed is [1] but was expected to be [2]");
        history.assertOneTransitionMarked(StepsFH.First, StepsFH.ResultA);
        SingularTestUtil.assertException(() -> history.assertNoTransitionMarked(StepsFH.First, StepsFH.ResultA),
                AssertionError.class, "There is 1 transition(s) from 'FIRSTX' to 'RESULTAX' marked as executed");
        SingularTestUtil.assertException(() -> history.assertOneTransitionMarked(StepsFH.ResultA, StepsFH.End),
                AssertionError.class, "There is no transition from 'RESULTAX' to 'ENDX'");
        history.assertNoTransitionMarked(StepsFH.ResultA, StepsFH.End);
        history.assertCurrentTask(StepsFH.ResultA);
        SingularTestUtil.assertException(() -> history.assertCurrentTask(StepsFH.End), AssertionError.class,
                "The current task was expcted to be [endx] but it is [resultax]");

        history.assertTaskMarked(StepsFH.First, StepsFH.ResultA);
        SingularTestUtil.assertException(() -> history.assertTaskMarked(StepsFH.First), AssertionError.class,
                "The tasks marked as executed isn't the expected ones");
        SingularTestUtil.assertException(() -> history.assertTaskMarked(StepsFH.First, StepsFH.ResultA, StepsFH.ResultB),
                AssertionError.class, "The tasks marked as executed isn't the expected ones");

        fi.getCurrentTaskOrException().prepareTransition().go();
        ExecutionHistoryForRendering history2 = ExecutionHistoryForRendering.from(fi, false);
        history2.debug();
        history2.assertTransitionMarked(2);
        history2.assertOneTransitionMarked(StepsFH.First, StepsFH.ResultA);
        history2.assertOneTransitionMarked(StepsFH.ResultA, StepsFH.End);
        history2.assertTaskMarked(StepsFH.First, StepsFH.ResultA, StepsFH.End);
    }

    @Test
    public void goToResultB() {
        FlowInstance fi = goToResult(StepsFH.ResultB);
        ExecutionHistoryForRendering history = ExecutionHistoryForRendering.from(fi, false);

        fi.getCurrentTaskOrException().prepareTransition("Go End2").go();
        ExecutionHistoryForRendering history2 = ExecutionHistoryForRendering.from(fi, false);
        //history2.debug();
        history2.assertTransitionMarked(2);
        history2.assertOneTransitionMarked(StepsFH.First, StepsFH.ResultB);
        history2.assertOneTransitionMarked(StepsFH.ResultB, StepsFH.End, "goend2");
        history2.assertTaskMarked(StepsFH.First, StepsFH.ResultB, StepsFH.End);
        SingularTestUtil.assertException(
                () -> history2.assertOneTransitionMarked(StepsFH.ResultB, StepsFH.End, "goend1"), AssertionError.class,
                "but the transition is [goend2] instead the expected transition [goend1]");
    }

    private FlowInstance goToResult(StepsFH target) {
        action = target;
        FlowInstance pi = new FlowForHistory().prepareStartCall().createAndStart();
        assertReloadAssert(pi, p -> TestFlowSupport.assertions(p).isAtTask(target));
        return pi;
    }

    @DefinitionInfo("ForHistory")
    public static class FlowForHistory extends FlowDefinition<FlowInstance> {

        public enum StepsFH implements ITaskDefinition {
            First, ResultA, ResultB, End;

            @Override
            public String getName() {
                return toString() + " X";
            }
        }

        public FlowForHistory() {
            super(FlowInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addJavaTask(StepsFH.First).call(this::decideNextTask);
            f.addWaitTask(StepsFH.ResultA);
            f.addWaitTask(StepsFH.ResultB);
            f.addEndTask(StepsFH.End);

            f.setStartTask(StepsFH.First);
            f.from(StepsFH.First).go(StepsFH.ResultA).thenGo(StepsFH.End);
            f.from(StepsFH.First).go(StepsFH.ResultB);
            f.from(StepsFH.ResultB).go("Go End1", StepsFH.End);
            f.from(StepsFH.ResultB).go("Go End2", StepsFH.End);

            return f.build();
        }

        Object decideNextTask(ExecutionContext executionContext) {
            if (action != null) {
                executionContext.setTransition(action.getName());
            }
            return null;
        }
    }
}

