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
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.TesFlowMapValidations.FlowWithFlowValidation.StepsDI;
import org.opensingular.flow.core.builder.BuilderHuman;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.property.MetaDataKey;
import org.opensingular.internal.lib.commons.test.RunnableEx;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.schedule.ScheduleDataBuilder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TesFlowMapValidations {

    private static final MetaDataKey<Boolean> TAG = MetaDataKey.of("tag", Boolean.class);
    private static final MetaDataKey<Boolean> FLAG = MetaDataKey.of("flag", Boolean.class);

    private static ValidationConditions condicions = new ValidationConditions();

    @After
    @Before
    public void correctCondicions() {
        condicions = new ValidationConditions();
    }

    @Test
    public void basic() {
        condicions = new ValidationConditions();
        FlowWithFlowValidation definition = new FlowWithFlowValidation();

        assertException(() -> definition.getFlowMap().getTaskByAbbreviationOrException("wrong"), "not found");
        definition.getFlowMap().getTaskByAbbreviationOrException(StepsDI.StepPeople.getKey());
        assertException(() -> definition.getFlowMap().getHumanTaskByAbbreviationOrException("wrong"), "not found");
        definition.getFlowMap().getHumanTaskByAbbreviationOrException(StepsDI.StepPeople.getKey());
        assertException(() -> definition.getFlowMap().getHumanTaskByAbbreviationOrException(StepsDI.StepWait.getKey()), "found, but it is of type");
        assertException(() -> definition.getFlowMap().getTask(StepsDI.NoAndded), "não encontrada");

        List<STask<?>> result = definition.getFlowMap().getAllTasks().stream().filter(
                task -> task.getMetaDataValueOpt(TAG).isPresent()).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertTrue(result.get(0).is(StepsDI.StepWait));

        assertTrue(definition.getFlowMap().getTask(StepsDI.StepWait).getMetaData().getOpt(FLAG).orElse(Boolean.FALSE));
    }

    @Test
    public void dontSetStart() {
        condicions = new ValidationConditions();
        condicions.configStart = false;
        assertException(() -> new FlowWithFlowValidation().getFlowMap(), "There is no initial task set");
    }

    @Test
    public void doesNotConfigHumanTask() {
        condicions = new ValidationConditions();
        condicions.configPeopleAccessStrategy = false;
        assertException(() -> new FlowWithFlowValidation().getFlowMap(), "Não foi definida a estrategia de verificação de acesso da tarefa");

        condicions = new ValidationConditions();
        condicions.configPeopleExecutionPage = false;
        assertException(() -> new FlowWithFlowValidation().getFlowMap(), "Não foi definida a estratégia da página para execução da tarefa");
    }

    @Test
    public void taskWithoutPathToEndWithoutTransition() {
        assertException(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addWaitTask(first);
            f.addEndTask(end);

            f.setStartTask(first);
        }, "no way to reach the end (without out transition)");
    }

    @Test
    public void taskWithoutPathToEndWithSelfReference() {
        assertException(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addWaitTask(first);
            f.addEndTask(end);

            f.setStartTask(first);
            f.from(first).go("selfReference", first);
        }, "no way to reach the end (without out transition)");
    }

    @Test
    public void taskWithoutPathToEndWithCircularReference() {
        assertException(f -> {
            ITaskDefinition first = ITaskDefinition.of("First");
            ITaskDefinition second = ITaskDefinition.of("Second");
            ITaskDefinition end = ITaskDefinition.of("End");

            f.addWaitTask(first);
            f.addWaitTask(second);
            f.addEndTask(end);

            f.setStartTask(first);
            f.from(first).go(second).thenGo(first);
        }, "no way to reach the end (circular reference)");
    }

    public static void assertException(@Nonnull Consumer<FlowBuilderImpl> flowCreator, String expectedExceptionMsg) {
        assertException(() -> SFlowUtil.instanceForDebug(flowCreator).getFlowMap(), expectedExceptionMsg);
    }

    @Test
    public void flowMetaData() {
        condicions = new ValidationConditions();
        FlowWithFlowValidation p = new FlowWithFlowValidation();
        p.getFlowMap().setMetaDataValue(TAG, Boolean.TRUE);
        assertTrue(p.getMetaDataValueOpt(TAG).orElse(Boolean.FALSE));
        p.getFlowMap().setMetaDataValue(TAG, Boolean.FALSE);
        assertFalse(p.getMetaDataValueOpt(TAG).orElse(Boolean.TRUE));
    }

    @Test
    public void taskJavaWithoutCall() {
        condicions = new ValidationConditions();
        condicions.javaTaskSetCode = false;
        assertException(() -> new FlowWithFlowValidation().getFlowMap(), "Não foi configurado o código de execução da tarefa");
    }

    @Test
    public void taskJavaWithBatchCall() {
        condicions = new ValidationConditions();
        condicions.javaTaskSetCode = false;
        condicions.javaTaskSetCodeByBlock = true;
        new FlowWithFlowValidation().getFlowMap();
    }

    @DefinitionInfo("WithFlowValidation")
    public static class FlowWithFlowValidation extends FlowDefinition<FlowInstance> {

        public enum StepsDI implements ITaskDefinition {
            StepWait("F1"), StepWait2("F1"), StepPeople("S1"), StepJava("J1"), End("E1"), NoAndded("X");

            private final String name;

            StepsDI(String name) {this.name = name;}

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getKey() {
                return toString();
            }
        }

        public FlowWithFlowValidation() {
            super(FlowInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            BuilderHuman<?> humanTask = f.addHumanTask(StepsDI.StepPeople);
            if (condicions.configPeopleExecutionPage) {
                humanTask.withExecutionPage((t, u) -> null);
            }
            if (condicions.configPeopleAccessStrategy) {
                humanTask.uiAccess(SFlowUtil.dummyTaskAccessStrategy());
            }

            f.addWaitTask(StepsDI.StepWait).setMetaDataValue(TAG, Boolean.TRUE);

            assertException(() -> f.addWaitTask(StepsDI.StepWait), "Task with abbreviation");
            assertException(() -> f.addWaitTask(StepsDI.StepWait2), "Task with name");

            if (condicions.javaTaskSetCodeByBlock) {
                f.addJavaTask(StepsDI.StepJava).batchCall((TaskJavaBatchCall) (task) -> null,
                        ScheduleDataBuilder.buildMinutely(60));
            } else if (condicions.javaTaskSetCode) {
                f.addJavaTask(StepsDI.StepJava).call( (task) -> {});
            } else {
                f.addJavaTask(StepsDI.StepJava);
            }

            f.addEndTask(StepsDI.End);
            assertException(() -> f.addEndTask(StepsDI.End), "already defined");

            assertException(() -> f.build().getStart(), "Task inicial não definida no fluxo");

            if (condicions.configStart) {
                f.setStartTask(StepsDI.StepWait);
                assertException(() -> f.addStartTask(StepsDI.StepWait), "This task is already defined as a start point");
                assertException(() -> f.setStartTask(StepsDI.StepJava), "The start point is already set");
            }

            f.from(StepsDI.StepWait).go(StepsDI.StepPeople).thenGo(StepsDI.StepJava);
            f.from(StepsDI.StepJava).go(StepsDI.End);

            f.forEach(builder -> builder.setMetaDataValue(FLAG, Boolean.TRUE));
            return f.build();
        }
    }

    public static void assertException(RunnableEx code, String expectedExceptionMsgPart) {
        SingularTestUtil.assertException(code, SingularFlowException.class, expectedExceptionMsgPart, null);
    }

    private static class ValidationConditions {
        public boolean configStart = true;
        public boolean configPeopleExecutionPage = true;
        public boolean configPeopleAccessStrategy = true;
        public boolean javaTaskSetCode = true;
        public boolean javaTaskSetCodeByBlock = false;
    }
}

