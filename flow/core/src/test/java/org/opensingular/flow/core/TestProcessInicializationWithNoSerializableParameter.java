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
import org.opensingular.flow.core.TestProcessInicializationWithNoSerializableParameter
        .ProcessWithNoSerializableStartParameter.StepsNS;
import org.opensingular.flow.core.builder.BuilderStart;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.variable.SingularFlowConvertingValueException;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin on 18/03/2017.
 */
public class TestProcessInicializationWithNoSerializableParameter extends TestFlowExecutionSupport {

    private static final Date VALUE_DT = new Date();

    private static final String PARAM_FLAG = "paramFlag";
    private static final String PARAM_NO_SERIALIZABLE = "paramNS";
    private static final String PARAM_SERIALIZABLE = "paramS";
    private static final String VAR_FLAG = "varFlag";

    private static boolean startInitializerCalled;

    @Test
    public void corretCall() {
        startInitializerCalled = false;
        StartCall<ProcessInstance> startCall = new ProcessWithNoSerializableStartParameter().prepareStartCall()
                .setValue(PARAM_FLAG, "y")
                .setValue(PARAM_NO_SERIALIZABLE, new MyNoSerializable("x"));

        ProcessInstance pi = startCall.createAndStart();
        assertTrue(startInitializerCalled);

        assertions(pi).isAtTask(StepsNS.First)
                .isVariableValue(VAR_FLAG, "x")
                .isDescription("x")
                .isVariablesSize(1, 1);
    }

    @Test
    public void serializationOk() {
        startInitializerCalled = false;
        StartCall<ProcessInstance> startCall = new ProcessWithNoSerializableStartParameter().prepareStartCall()
                .setValue(PARAM_FLAG, "y");

        startCall = SingularIOUtils.serializeAndDeserialize(startCall, true);

        ProcessInstance pi = startCall.createAndStart();
        assertTrue(startInitializerCalled);

        assertions(pi).isAtTask(StepsNS.First)
                .isVariableValue(VAR_FLAG, "y")
                .isDescription("y")
                .isVariablesSize(1, 1);
    }

    @Test
    public void serializationOkWithCustomParam() {
        startInitializerCalled = false;
        StartCall<ProcessInstance> startCall = new ProcessWithNoSerializableStartParameter().prepareStartCall()
                .setValue(PARAM_FLAG, "y")
                .setValue(PARAM_SERIALIZABLE, new MySerializable("z"));

        startCall = SingularIOUtils.serializeAndDeserialize(startCall, true);

        ProcessInstance pi = startCall.createAndStart();
        assertTrue(startInitializerCalled);

        assertions(pi).isAtTask(StepsNS.First)
                .isVariableValue(VAR_FLAG, "z")
                .isDescription("z")
                .isVariablesSize(1, 1);
    }

    @Test
    public void serializationErro() {
        StartCall<ProcessInstance> startCall = new ProcessWithNoSerializableStartParameter().prepareStartCall()
                .setValue(PARAM_FLAG, "y")
                .setValue(PARAM_NO_SERIALIZABLE, new MyNoSerializable("x"));

        SingularTestUtil.assertException(() -> SingularIOUtils.serializeAndDeserialize(startCall, true),
                NotSerializableException.class, "MyNoSerializable");
    }

    @Test
    public void setingAParamWithDiferenteClassTypeOfTheExpected() {
        StartCall<ProcessInstance> startCall = new ProcessWithNoSerializableStartParameter().prepareStartCall();
        SingularTestUtil.assertException(() -> startCall.setValue(PARAM_SERIALIZABLE, new MyNoSerializable("z")),
                SingularFlowConvertingValueException.class, "Não foi possível converter ");
    }

    @DefinitionInfo("WithNoSerializableParameter")
    public static class ProcessWithNoSerializableStartParameter extends ProcessDefinition<ProcessInstance> {

        public enum StepsNS implements ITaskDefinition {
            First, Second, End;

            @Override
            public String getName() {
                return toString();
            }
        }

        public ProcessWithNoSerializableStartParameter() {
            super(ProcessInstance.class);
            getVariables().addVariableString(VAR_FLAG);
        }

        @Override
        protected FlowMap createFlowMap() {
            FlowBuilderImpl f = new FlowBuilderImpl(this);

            f.addWaitTask(StepsNS.First);
            f.addWaitTask(StepsNS.Second);
            f.addEnd(StepsNS.End);

            f.setStart(StepsNS.First).setInitializer(this::processInitializer).with(this::setupStartParameters);
            f.from(StepsNS.First).go(StepsNS.Second).thenGo(StepsNS.End);

            return f.build();
        }

        private void setupStartParameters(BuilderStart<?> start) {
            start.addParamString(PARAM_FLAG, true);
            start.addParamCustom(PARAM_NO_SERIALIZABLE, MyNoSerializable.class, false);
            start.addParamCustom(PARAM_SERIALIZABLE, MySerializable.class, false);
        }

        private void processInitializer(ProcessInstance instance, StartCall<ProcessInstance> startCall) {
            startInitializerCalled = true;
            String flag = startCall.getValueString(PARAM_FLAG);
            MyNoSerializable v = startCall.getValue(PARAM_NO_SERIALIZABLE);
            MySerializable v2 = startCall.getValue(PARAM_SERIALIZABLE);
            if (v != null) {
                instance.setVariable(VAR_FLAG, v.getValue());
            } else if (v2 != null) {
                instance.setVariable(VAR_FLAG, v2.getValue());
            } else {
                instance.setVariable(VAR_FLAG, flag);
            }
            instance.setDescription(instance.getVariableValueString(VAR_FLAG));

            instance.start();
        }
    }

    static class MyNoSerializable {

        private final String value;

        public MyNoSerializable(String value) {this.value = value;}

        public String getValue() {
            return value;
        }
    }

    static class MySerializable implements Serializable {

        private final String value;

        public MySerializable(String value) {this.value = value;}

        public String getValue() {
            return value;
        }
    }
}

