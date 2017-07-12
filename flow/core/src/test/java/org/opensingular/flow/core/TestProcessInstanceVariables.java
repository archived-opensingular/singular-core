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

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.TestProcessInstanceVariables.FlowWithVariables.StepsPV;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProcessInstanceVariables extends TestFlowExecutionSupport {

    private static final String PARAM_STRING = "paramString";
    private static final String PARAM_STRING_VALUE = "x";
    private static final String PARAM_INTEGER = "paramInteger";
    private static final Integer PARAM_INTEGER_VALUE = 20;
    private static final String PARAM_DATE = "paramDate";
    private static Date PARAM_DATE_VALUE;
    private static final String PARAM_BOOLEAN = "paramBoolean";
    private static final Boolean PARAM_BOOLEAN_VALUE = Boolean.TRUE;
    private static final String PARAM_NULL = "paramNull";

    @Before
    public void setUpDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 2);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 57);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        PARAM_DATE_VALUE = cal.getTime();
    }

    @Test
    public void simpleVriables() {
        FlowInstance pi = new FlowWithVariables().prepareStartCall().createAndStart();

        assertReloadAssert(pi, p-> {
            assertions(p).isAtTask(StepsPV.First)
                    .isVariableValue(PARAM_STRING, PARAM_STRING_VALUE)
                    .isVariableValue(PARAM_NULL, null)
                    .isVariableValue(PARAM_BOOLEAN, PARAM_BOOLEAN_VALUE)
                    .isVariableValue(PARAM_DATE, PARAM_DATE_VALUE)
                    .isVariableValue(PARAM_INTEGER, PARAM_INTEGER_VALUE);
            assertEquals(PARAM_STRING_VALUE, pi.getVariableValueString(PARAM_STRING));
            assertEquals(PARAM_BOOLEAN_VALUE, pi.getVariableValueBoolean(PARAM_BOOLEAN));
            assertEquals(PARAM_INTEGER_VALUE, pi.getVariableValueInteger(PARAM_INTEGER));
        });
    }

    @Test
    public void tryToReadInvalidVariable() {
        FlowInstance pi = new FlowWithVariables().prepareStartCall().createAndStart();

        assertTrue(pi.getVariables().contains(PARAM_STRING));
        assertFalse(pi.getVariables().contains("asd"));
        SingularTestUtil.assertException(() -> pi.getVariableValue("asd"), SingularFlowException.class,
                "não está definida");

    }

    @DefinitionInfo("WithVariables")
    public static class FlowWithVariables extends FlowDefinition<FlowInstance> {

        public enum StepsPV implements ITaskDefinition {
            First, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public FlowWithVariables() {
            super(FlowInstance.class);
            getVariables().addVariableString(PARAM_STRING);
            getVariables().addVariableString(PARAM_NULL);
            getVariables().addVariableInteger(PARAM_INTEGER);
            getVariables().addVariableBoolean(PARAM_BOOLEAN);
            getVariables().addVariableDate(PARAM_DATE);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addWaitTask(StepsPV.First);
            f.addEnd(StepsPV.End);

            f.setStart(StepsPV.First).setInitializer(this::processInitializer);
            f.from(StepsPV.First).go(StepsPV.End);

            return f.build();
        }

        private void processInitializer(FlowInstance instance, StartCall<FlowInstance> startCall) {
            instance.getVariables().setValue(PARAM_STRING, PARAM_STRING_VALUE);
            instance.setVariable(PARAM_DATE, PARAM_DATE_VALUE);
            instance.setVariable(PARAM_BOOLEAN, PARAM_BOOLEAN_VALUE);
            instance.setVariable(PARAM_INTEGER, PARAM_INTEGER_VALUE);
            instance.start();
        }
    }
}

