/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.opensingular.flow.core.builder;

import org.opensingular.flow.core.*;
import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.flow.core.variable.VarType;

import java.util.Objects;

public class FlowBuilderImpl extends
        FlowBuilder<ProcessDefinition<?>, FlowMap, BTask, BJava<?>, BPeople<?>, BWait<?>, BEnd<?>, BTransition<?>, BProcessRole<?>, ITaskDefinition> {

    public FlowBuilderImpl(ProcessDefinition<?> processDefinition) {
        super(processDefinition);
    }

    @Override
    protected FlowMap newFlowMap(ProcessDefinition<?> processDefinition) {
        return new FlowMap(processDefinition);
    }

    @Override
    protected BTask newTask(MTask<?> task) {
        return new ImplBTask(this, task);
    }

    @Override
    protected BJava<?> newJavaTask(MTaskJava task) {
        return new ImplBJava<>(this, task);
    }

    @Override
    protected BPeople<?> newPeopleTask(MTaskPeople task) {
        return new ImplBPeople<>(this, task);
    }

    @Override
    protected BWait<?> newWaitTask(MTaskWait task) {
        return new ImplBWait<>(this, task);
    }

    @Override
    protected BEnd<?> newEndTask(MTaskEnd task) {
        return new ImplBEnd<>(this, task);
    }

    @Override
    protected BTransition<?> newTransition(MTransition transition) {
        return new ImplBTransition<>(this, transition);
    }

    @Override
    protected BProcessRole<?> newProcessRole(MProcessRole mProcessRole) {
        return new ImplBProcessRole<>(mProcessRole);
    }

    public static class ImplBTask<SELF extends ImplBTask<SELF, TASK>, TASK extends MTask<?>> implements BuilderTaskSelf<SELF, TASK> {

        private final FlowBuilder<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> flowBuilder;
        private final TASK task;

        public ImplBTask(FlowBuilder<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> flowBuilder, TASK task) {
            this.flowBuilder = flowBuilder;
            this.task = task;
        }

        @Override
        public TASK getTask() {
            return task;
        }

        @SuppressWarnings("rawtypes")
        protected FlowBuilder getFlowBuilder() {
            return flowBuilder;
        }

        /**
         * Cria uma nova transição da task atual para a task destino informada
         * com o nome informado.
         */
        public BTransition<?> go(String actionName, ITaskDefinition taskRefDestiny) {
            return getFlowBuilder().addTransition(this, actionName, taskRefDestiny);
        }

        /**
         * Cria uma nova transição da task atual para a task destino informada
         */
        public BTransition<?> go(ITaskDefinition taskRefDestiny) {
            return go(taskRefDestiny.getName(), taskRefDestiny);
        }
    }

    protected static class ImplBJava<SELF extends ImplBJava<SELF>> extends ImplBTask<SELF, MTaskJava> implements BJava<SELF> {
        public ImplBJava(FlowBuilderImpl flowBuilder, MTaskJava task) {
            super(flowBuilder, task);
        }
    }

    protected static class ImplBPeople<SELF extends ImplBPeople<SELF>> extends ImplBTask<SELF, MTaskPeople> implements BPeople<SELF> {
        public ImplBPeople(FlowBuilderImpl flowBuilder, MTaskPeople task) {
            super(flowBuilder, task);
        }
    }

    protected static class ImplBWait<SELF extends ImplBWait<SELF>> extends ImplBTask<SELF, MTaskWait> implements BWait<SELF> {
        public ImplBWait(FlowBuilderImpl flowBuilder, MTaskWait task) {
            super(flowBuilder, task);
        }
    }

    protected static class ImplBEnd<SELF extends ImplBEnd<SELF>> extends ImplBTask<SELF, MTaskEnd> implements BEnd<SELF> {
        public ImplBEnd(FlowBuilderImpl flowBuilder, MTaskEnd task) {
            super(flowBuilder, task);
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static class ImplBTransition<SELF extends ImplBTransition<SELF>> implements BTransition<SELF> {
        private final FlowBuilder flowBuilder;

        private final MTransition transition;

        public ImplBTransition(FlowBuilder flowBuilder, MTransition transition) {
            this.flowBuilder = flowBuilder;
            this.transition = transition;
        }

        @Override
        public MTransition getTransition() {
            return transition;
        }

        @Override
        public FlowBuilder getFlowBuilder() {
            return flowBuilder;
        }

        public SELF addParamFromProcessVariable(String ref, boolean obrigatorio) {
            getTransition().addParamFromProcessVariable(ref, obrigatorio);
            return self();
        }

        public SELF addParamString(String ref, boolean obrigatorio, Integer tamanho) {
            return addParamString(ref, ref, obrigatorio, tamanho);
        }

        public SELF addParamString(String ref, boolean obrigatorio) {
            return addParamString(ref, ref, obrigatorio, null);
        }

        public SELF addParamString(String ref, String nome, boolean obrigatorio) {
            return addParamString(ref, nome, obrigatorio, null);
        }

        public SELF addParamString(String ref, String nome, boolean obrigatorio, Integer tamanho) {
            getTransition().getParameters().addVariableString(ref, nome, tamanho).setRequired(obrigatorio);
            return self();
        }

        public SELF addParamStringMultipleLines(String ref, String nome, boolean obrigatorio) {
            return addParamStringMultipleLines(ref, nome, obrigatorio, null);
        }

        public SELF addParamStringMultipleLines(String ref, String nome, boolean obrigatorio, Integer tamanho) {
            getTransition().getParameters().addVariableStringMultipleLines(ref, nome, tamanho).setRequired(obrigatorio);
            return self();
        }

        public SELF addParamInteger(String ref, boolean obrigatorio) {
            return addParamInteger(ref, ref, obrigatorio);
        }

        public SELF addParamInteger(String ref, String nome, boolean obrigatorio) {
            getTransition().getParameters().addVariableInteger(ref, nome).setRequired(obrigatorio);
            return self();
        }

        public SELF addParamDouble(String ref, boolean obrigatorio) {
            return addParamDouble(ref, ref, obrigatorio);
        }

        public SELF addParamDouble(String ref, String nome, boolean obrigatorio) {
            getTransition().getParameters().addVariableDouble(ref, nome).setRequired(obrigatorio);
            return self();
        }

        public SELF addParamDate(String ref, boolean obrigatorio) {
            return addParamDate(ref, ref, obrigatorio);
        }

        public SELF addParamDate(String ref, String nome, boolean obrigatorio) {
            getTransition().getParameters().addVariableDate(ref, nome).setRequired(obrigatorio);
            return self();
        }

        public SELF addParam(String ref, VarType tipo, boolean obrigatorio) {
            return addParam(ref, ref, tipo, obrigatorio);
        }

        public SELF addParam(String ref, String nome, VarType varType, boolean obrigatorio) {
            getTransition().getParameters().addVariable(ref, nome, varType).setRequired(obrigatorio);
            return self();
        }

        public <K extends ProcessInstance> SELF setParametersInitializer(MTransition.ITransitionParametersProcessInitializer<K> parametrosInicializer) {
            getTransition().setParametersInitializer(parametrosInicializer);
            return self();
        }

        public <K extends ProcessInstance> SELF setParametersValidator(MTransition.ITransitionParametersProcessValidator<K> parametrosValidator) {
            getTransition().setParametersValidator(parametrosValidator);
            return self();
        }

        public <T> SELF setMetaDataValue(MetaDataRef<T> propRef, T value) {
            getTransition().setMetaDataValue(propRef, value);
            return self();
        }

    }

    public static class ImplBProcessRole<SELF extends ImplBProcessRole<SELF>> implements BProcessRole<SELF> {

        private final MProcessRole processRole;

        public ImplBProcessRole(MProcessRole processRole) {
            Objects.requireNonNull(processRole);
            this.processRole = processRole;
        }

        @Override
        public MProcessRole getProcessRole() {
            return processRole;
        }
    }

    @Override
    public void addListenerToAllTasks(StartedTaskListener listener) {
        for (MTask<?> mTask : getFlowMap().getAllTasks()) {
            mTask.addStartedTaskListener(listener);
        }
    }
}
