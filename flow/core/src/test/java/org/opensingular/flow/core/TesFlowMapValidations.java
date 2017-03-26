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
import org.opensingular.flow.core.TesFlowMapValidations.ProcessWithFlowValidation.StepsDI;
import org.opensingular.flow.core.builder.BuilderPeople;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TesFlowMapValidations {

    private static final MetaDataRef<Boolean> TAG = new MetaDataRef<>("tag", Boolean.class);

    private static ValidationCondicions condicions = new ValidationCondicions();

    @After
    @Before
    public void correctCondicions() {
        condicions = new ValidationCondicions();
    }

    @Test
    public void basic() {
        condicions = new ValidationCondicions();
        ProcessWithFlowValidation definition = new ProcessWithFlowValidation();

        assertException(() -> definition.getFlowMap().getTaskByAbbreviationOrException("wrong"), "not found");
        definition.getFlowMap().getTaskByAbbreviationOrException(StepsDI.Second.getKey());
        assertException(() -> definition.getFlowMap().getPeopleTaskByAbbreviationOrException("wrong"), "not found");
        definition.getFlowMap().getPeopleTaskByAbbreviationOrException(StepsDI.Second.getKey());
        assertException(() -> definition.getFlowMap().getPeopleTaskByAbbreviationOrException(StepsDI.First.getKey()), "found, but it is of type");
        assertException(() -> definition.getFlowMap().getTask(StepsDI.NoAndded), "não encontrada");

        List<STask<?>> result = definition.getFlowMap().getTasksWithMetadata(TAG);
        assertEquals(1, result.size());
        assertTrue(result.get(0).is(StepsDI.First));
    }

    @Test
    public void dontSetStart() {
        condicions = new ValidationCondicions();
        condicions.configStart = false;
        assertException(() -> new ProcessWithFlowValidation().getFlowMap(), "There is no initial task set");
    }

    @Test
    public void dontConfigPeopleTask() {
        condicions = new ValidationCondicions();
        condicions.configPeopleAccessStrategy = false;
        assertException(() -> new ProcessWithFlowValidation().getFlowMap(), "Não foi definida a estrategia de verificação de acesso da tarefa");

        condicions = new ValidationCondicions();
        condicions.configPeopleExecutionPage = false;
        assertException(() -> new ProcessWithFlowValidation().getFlowMap(), "Não foi definida a estratégia da página para execução da tarefa");
    }

    @Test
    public void taskWithoutPathToEnd() {
        condicions = new ValidationCondicions();
        condicions.createTaskWithoutPathToEnd = true;
        assertException(() -> new ProcessWithFlowValidation().getFlowMap(), "no way to reach the end");
    }

    @Test
    public void flowMetaData() {
        condicions = new ValidationCondicions();
        ProcessWithFlowValidation p = new ProcessWithFlowValidation();
        p.getFlowMap().setMetaDataValue(TAG, Boolean.TRUE);
        assertTrue(p.getMetaDataValue(TAG));
        p.getFlowMap().setMetaDataValue(TAG, Boolean.FALSE);
        assertFalse(p.getMetaDataValue(TAG));

    }

    @DefinitionInfo("WithFlowValidation")
    public static class ProcessWithFlowValidation extends ProcessDefinition<ProcessInstance> {

        public enum StepsDI implements ITaskDefinition {
            First("F1"), First2("F1"), Second("S1"), End("E1"), NoAndded("X");

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

        public ProcessWithFlowValidation() {
            super(ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            BuilderPeople<?> peopleTask = f.addPeopleTask(StepsDI.Second);
            if (condicions.configPeopleExecutionPage) {
                peopleTask.withExecutionPage((t, u) -> null);
            }
            if (condicions.configPeopleAccessStrategy) {
                peopleTask.addAccessStrategy(new DummyAccessStrategy());
            }

            f.addWaitTask(StepsDI.First).setMetaDataValue(TAG, Boolean.TRUE);

            assertException(() -> f.addWaitTask(StepsDI.First), "Task with abbreviation");
            assertException(() -> f.addWaitTask(StepsDI.First2), "Task with name");

            f.addEnd(StepsDI.End);
            assertException(() -> f.addEnd(StepsDI.End), "already defined");


            assertException(() -> f.build().getStart(), "Task inicial não definida no processo");

            if (condicions.configStart) {
                f.setStart(StepsDI.First);
                assertException(() -> f.setStart(StepsDI.First), "The start point is already setted");
            }

            f.from(StepsDI.First).go(StepsDI.Second);

            if (! condicions.createTaskWithoutPathToEnd) {
                f.from(StepsDI.Second).go(StepsDI.End);
            }

            return f.build();
        }
    }

    public static void assertException(Runnable code, String expectedExceptionMsgPart) {
        SingularTestUtil.assertException(code, SingularFlowException.class, expectedExceptionMsgPart, null);
    }

    private static class ValidationCondicions {
        public boolean configStart = true;
        public boolean configPeopleExecutionPage = true;
        public boolean configPeopleAccessStrategy = true;
        public boolean createTaskWithoutPathToEnd = false;
    }


    public static class DummyAccessStrategy extends TaskAccessStrategy<ProcessInstance> {

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
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, STask<?> task) {
            return null;
        }
    }
}

