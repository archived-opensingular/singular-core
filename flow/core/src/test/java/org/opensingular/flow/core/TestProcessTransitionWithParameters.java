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

import org.junit.Test;
import org.opensingular.flow.core.TestProcessTransitionWithParameters.FlowTransitionWithParameters.StepsTP;
import org.opensingular.flow.core.builder.BuilderStart;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
public class TestProcessTransitionWithParameters extends TestFlowExecutionSupport {

    private static final Date VALUE_DT = new Date();

    private static final String PARAM_FLAG = "paramFlag";
    private static final String PARAM_NOCOPY = "paramNoCopy";
    private static final String PARAM_BIG = "paramBig";
    private static final BigDecimal VALUE_BIG1 = new BigDecimal(10);
    private static final BigDecimal VALUE_BIG2 = new BigDecimal(12);


    @Nonnull
    private FlowInstance createTestProcess() {

        StartCall<FlowInstance> startCall = new FlowTransitionWithParameters().prepareStartCall()
                .setValue(PARAM_FLAG, "A")
                .setValue(PARAM_BIG, VALUE_BIG1);
        return startCall.createAndStart();
    }

    @Test
    public void callTransition0_withParam() {
        runTransition(this::createTestProcess, null,
                call -> {
                    call.setValue(PARAM_FLAG, "B");
                    call.setValue(PARAM_BIG, VALUE_BIG2);
                    call.setValue(PARAM_NOCOPY, 2);
                },
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Second)
                            .isVariableValue(PARAM_FLAG, "B")
                            .isVariableValue(PARAM_BIG,VALUE_BIG2);
                });
    }

    @Test
    public void callTransition0_withNoParam() {
        runTransition(this::createTestProcess, null,
                call -> {},
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Second)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG,VALUE_BIG1)
                            .isTaskSequence(StepsTP.First, StepsTP.Second);
                });
    }

    @Test
    public void callTransition0_twice() {
        runTransition(this::createTestProcess, null,
                call -> call.go(),
                SingularFlowException.class,
                "já está concluida",
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Second)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG,VALUE_BIG1)
                            .isTaskSequence(StepsTP.First, StepsTP.Second);
                });
    }

    @Test
    public void callTransition1_withoutRequiredParam() {
        runTransition(this::createTestProcess, "transition1",
                call -> {
                },
                SingularFlowInvalidParametersException.class,
                "parametro obrigatório não informado",
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Third)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG, VALUE_BIG1);
                });
    }

    @Test
    public void callTransition2() {
        runTransition(this::createTestProcess, "transition2",
                call -> {
                    call.setValue(PARAM_FLAG, "B");
                    call.setValue(PARAM_BIG, VALUE_BIG2);
                    call.setValue(PARAM_NOCOPY, 2);
                },
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Third)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG, VALUE_BIG1);
                });
    }

    @Test
    public void callTransition2_withoutRequiredParam() {
        runTransition(this::createTestProcess, "transition2",
                call -> {
                },
                SingularFlowInvalidParametersException.class,
                "parametro obrigatório não informado",
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Third)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG, VALUE_BIG1);
                });
    }

    @Test
    public void callTransition2_withInvalidParam() {
        runTransition(this::createTestProcess, "transition2",
                call -> {
                    call.setValue(PARAM_FLAG, "C");
                    call.setValue(PARAM_NOCOPY, 200);
                },
                SingularFlowInvalidParametersException.class,
                "Valor > 100",
                pi -> {
                    assertions(pi).isAtTask(StepsTP.Third)
                            .isVariableValue(PARAM_FLAG, "A")
                            .isVariableValue(PARAM_BIG, VALUE_BIG1);
                });
    }

    private void runTransition(Supplier<FlowInstance> processInstanceSupplier, String transitionName,
            Consumer<TransitionCall> callConfiguration, Consumer<FlowInstance> assertionsCode) {
        runTransition(processInstanceSupplier, transitionName, callConfiguration, null, null, assertionsCode);
    }

    private void runTransition(Supplier<FlowInstance> processInstanceSupplier, String transitionName,
            Consumer<TransitionCall> callConfiguration, Class<? extends Exception> expectedException,
            String expectedExceptionMsgPart, Consumer<FlowInstance> assertionsCode) {
        FlowInstance pi = processInstanceSupplier.get();
        TransitionCall transitionCall = createTrasaction(transitionName, pi);
        callConfiguration.accept(transitionCall);
        callTransition(transitionCall, expectedException, expectedExceptionMsgPart);
        assertReloadAssert(pi, assertionsCode);

        //Now again but with a lot of serializations
        pi = processInstanceSupplier.get();
        pi = SingularIOUtils.serializeAndDeserialize(pi, true);
        transitionCall = createTrasaction(transitionName, pi);
        transitionCall = SingularIOUtils.serializeAndDeserialize(transitionCall, true);
        callConfiguration.accept(transitionCall);
        transitionCall = SingularIOUtils.serializeAndDeserialize(transitionCall, true);
        callTransition(transitionCall, expectedException, expectedExceptionMsgPart);
        assertionsCode.accept(pi);
        pi = SingularIOUtils.serializeAndDeserialize(pi, true);
        assertReloadAssert(pi, assertionsCode);
    }

    private void callTransition(TransitionCall transitionCall, Class<? extends Exception> expectedException,
            String expectedExceptionMsgPart) {
        if (expectedException == null) {
            transitionCall.go();
        } else {
            SingularTestUtil.assertException(() -> transitionCall.go(), expectedException, expectedExceptionMsgPart);
        }
    }

    private TransitionCall createTrasaction(String transitionName, FlowInstance pi) {
        TaskInstance task = pi.getCurrentTask().orElseThrow(() -> new NullPointerException("task?"));
        if (transitionName == null) {
            return task.prepareTransition();
        }
        return task.prepareTransition(transitionName);
    }

    @DefinitionInfo("TransitionWithParameters")
    public static class FlowTransitionWithParameters extends FlowDefinition<FlowInstance> {

        public enum StepsTP implements ITaskDefinition {
            First, Second, Third, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public FlowTransitionWithParameters() {
            super(FlowInstance.class);
            getVariables().addVariableString(PARAM_FLAG);
            getVariables().addVariableBigDecimal(PARAM_BIG);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addWaitTask(StepsTP.First);
            f.addWaitTask(StepsTP.Second);
            f.addWaitTask(StepsTP.Third);
            f.addEndTask(StepsTP.End);

            f.setStartTask(StepsTP.First).with(this::setupStartParameters);

            f.from(StepsTP.First).go(StepsTP.Second)
                    .setAsDefaultTransition()
                    .addParamBindedToProcessVariable(PARAM_FLAG, false)
                    .addParamBindedToProcessVariable(PARAM_BIG, false)
                    .addParamInteger(PARAM_NOCOPY, false);

            f.from(StepsTP.First).go("transition1", StepsTP.Third)
                    .addParamBindedToProcessVariable(PARAM_FLAG, true)
                    .addParamBindedToProcessVariable(PARAM_BIG, false)
                    .addParamInteger(PARAM_NOCOPY, false);

            f.from(StepsTP.First).go("transition2", StepsTP.Third)
                    .addParamString(PARAM_FLAG, true)
                    .addParamBigDecimal(PARAM_BIG, false)
                    .addParamInteger(PARAM_NOCOPY, false)
                    .setParametersValidator(this::validateParamTransition2);

            f.from(StepsTP.Second).go(StepsTP.Third).thenGo(StepsTP.End);

            return f.build();
        }

        private <K extends FlowInstance> void validateParamTransition2(VarInstanceMap<?, ?> vars,
                ValidationResult result, K process) {
            Integer v = vars.getValueInteger(PARAM_NOCOPY);
            if (v != null && v > 100) {
                result.addErro(vars.getVariable(PARAM_NOCOPY), "Valor > 100");
            }
        }

        private void setupStartParameters(BuilderStart<?> start) {
            start.addParamBindedToProcessVariable(PARAM_FLAG, false);
            start.addParamBindedToProcessVariable(PARAM_BIG, false);
        }
    }
}

