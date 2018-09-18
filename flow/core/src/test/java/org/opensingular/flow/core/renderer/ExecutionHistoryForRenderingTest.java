package org.opensingular.flow.core.renderer;

import org.junit.Test;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ITaskDefinition;
import org.opensingular.flow.core.SFlowUtil;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.internal.lib.commons.test.TimeMaker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-19
 */
public class ExecutionHistoryForRenderingTest {

    @Test
    public void guessTransitions1() {
        ExecutionHistoryForRendering history = new ExecutionHistoryForRendering();

        runBuilder(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition second = ITaskDefinition.of("Second");
            ITaskDefinition third = ITaskDefinition.of("Third");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addHumanTask(first, SFlowUtil.dummyTaskAccessStrategy());
            f.addWaitTask(second);
            f.addJavaTask(third).call(SFlowUtil.dummyTaskJavaCall());
            f.addEndTask(end);

            f.setStartTask(first);
            f.from(first).go(second).setAsDefaultTransition();
            f.from(first).go("transition1", third);
            f.from(first).go("transition2", third);
            f.from(second).go(third).thenGo(end);

            FlowMap map = f.build();

            history.addExecuted(first);
            history.addExecuted(second);
            history.addExecuted(third);
            history.setCurrent(end);


            history.assertTransitionMarked(0);
            assertEquals(3, history.guessMissingTransitions(f.build()));
            history.assertTransitionMarked(3);
            history.assertOneTransitionMarked(first, second);
            history.assertOneTransitionMarked(second, third);
            history.assertOneTransitionMarked(third, end);

            assertFalse(history.isExecuted(map.getTask(first).getTransition("transition1").orElse(null)));
            assertTrue(history.isExecuted(map.getTask(first).getDefaultTransition()));
            assertTrue(history.isCurrent(end));
            assertTrue(history.isCurrent(map.getTask(end)));

        }, history);
    }

    @Test
    public void guessTransitions2() {
        ExecutionHistoryForRendering history = new ExecutionHistoryForRendering();
        runBuilder(f -> {
            ITaskDefinition task1 = ITaskDefinition.of("First");
            ITaskDefinition task2 = ITaskDefinition.of("Second");
            ITaskDefinition task3 = ITaskDefinition.of("Third");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addHumanTask(task1, SFlowUtil.dummyTaskAccessStrategy());
            f.addWaitTask(task2);
            f.addJavaTask(task3).call(SFlowUtil.dummyTaskJavaCall());
            f.addEndTask(end);

            f.setStartTask(task1);
            f.from(task1).go(task2);
            f.from(task2).go(task1);
            f.from(task1).go("aprovar", task3);
            f.from(task3).go("rejeitar", task2);
            f.from(task3).go(end);

            history.addExecuted(task1);
            history.addExecuted(task2);
            history.addExecuted(task3);
            history.setCurrent(end);

            history.assertTransitionMarked(0);
            assertEquals(3, history.guessMissingTransitions(f.build()));
            history.assertTransitionMarked(3);
            history.assertOneTransitionMarked(task1, task3);
            history.assertOneTransitionMarked(task2, task1);
            history.assertOneTransitionMarked(task3, end);

            ExecutionHistoryForRendering history2 = new ExecutionHistoryForRendering();
            assertEquals(0, history2.countTransitions());
            assertNull(history2.getCurrent());
            assertFalse(history2.hasAnyTransitionExecuted());
            assertTrue(history2.isEmpty());

            history2.addExecuted(task1);
            history2.addExecuted(task3);
            history2.setCurrent(end);

            history2.assertTransitionMarked(0);
            assertEquals(2, history2.guessMissingTransitions(f.build()));
            history2.assertTransitionMarked(2);
            history2.assertOneTransitionMarked(task1, task3);
            history2.assertOneTransitionMarked(task3, end);

            history2.clear();
            assertEquals(0, history2.countTransitions());
            assertNull(history2.getCurrent());
            assertFalse(history2.hasAnyTransitionExecuted());
            assertTrue(history2.isEmpty());

        }, history);
    }

    @Test
    public void guessTransitions3() {
        ExecutionHistoryForRendering history = new ExecutionHistoryForRendering();

        runBuilder(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition second = ITaskDefinition.of("Second");
            ITaskDefinition third = ITaskDefinition.of("Third");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addHumanTask(first, SFlowUtil.dummyTaskAccessStrategy());
            f.addWaitTask(second);
            f.addJavaTask(third).call(SFlowUtil.dummyTaskJavaCall());
            f.addEndTask(end);

            f.setStartTask(first);
            f.from(first).go(second);
            f.from(second).go(first);
            f.from(first).go("aprovar", third);
            f.from(second).go(end);
            f.from(third).go("rejeitar", second);
            f.from(third).go(end);

            history.addExecuted(first);
            history.addExecuted(second);
            history.addExecuted(third);
            history.setCurrent(end);

            history.assertTransitionMarked(0);
            assertEquals(1, history.guessMissingTransitions(f.build()));
            history.assertTransitionMarked(1);
            history.assertOneTransitionMarked(first, third);
        }, history);
    }

    @Test
    public void guessTransitionsWithTimestamp() {
        ExecutionHistoryForRendering history = new ExecutionHistoryForRendering();
        runBuilder(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition second = ITaskDefinition.of("Second");
            ITaskDefinition third = ITaskDefinition.of("Third");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addHumanTask(first, SFlowUtil.dummyTaskAccessStrategy());
            f.addWaitTask(second);
            f.addJavaTask(third).call(SFlowUtil.dummyTaskJavaCall());
            f.addEndTask(end);

            f.setStartTask(first);
            f.from(first).go(second);
            f.from(second).go(first);
            f.from(first).go("aprovar", third);
            f.from(second).go(end);
            f.from(third).go("rejeitar", second);
            f.from(third).go(end);

            TimeMaker timer = new TimeMaker();
            history.addExecuted(first, timer.get(), timer.incMinutes(10));
            history.addExecuted(third, timer.get(), timer.incMinutes(10));
            history.addExecuted(second, timer.get(), timer.incMinutes(10));
            history.addExecuted(end, timer.get(), null);

            history.assertTransitionMarked(0);
            assertEquals(4, history.guessMissingTransitions(f.build()));
            history.assertTransitionMarked(3);
            history.assertOneTransitionMarked(first, third);
            history.assertOneTransitionMarked(third, second);
            history.assertOneTransitionMarked(second, end);
            assertEquals("end", history.getCurrent());

            timer = new TimeMaker();
            ExecutionHistoryForRendering history2 = new ExecutionHistoryForRendering();
            history2.addExecuted(first, timer.get(), timer.incMinutes(10));
            history2.addExecuted(second, timer.get(), timer.incMinutes(10));
            history2.addExecuted(first, timer.get(), timer.incMinutes(10));
            history2.addExecuted(third, timer.incMinutes(10), timer.incMinutes(10));
            history2.addExecuted(end, timer.get(), null);

            history2.assertTransitionMarked(0);
            assertEquals(5, history2.guessMissingTransitions(f.build()));
            history2.assertTransitionMarked(4);
            history2.assertOneTransitionMarked(first, second);
            history2.assertOneTransitionMarked(second, first);
            history2.assertOneTransitionMarked(first, third);
            history2.assertOneTransitionMarked(third, end);
            history2.assertCurrentTask("end");
            assertEquals("end", history2.getCurrent());
        }, history);
    }

    private void runBuilder(@Nonnull Consumer<FlowBuilderImpl> flowCreator, @Nullable ExecutionHistoryForRendering history) {
        FlowDefinition<?> definition = SFlowUtil.instanceForDebug(flowCreator);
        definition.getFlowMap(); //Forces the flow to be created
    }
}