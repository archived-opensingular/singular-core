package br.net.mirante.singular.flow.core.builder;

import java.util.Objects;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;
import br.net.mirante.singular.flow.core.MTaskWait;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.MTransition.ITransitionParametersProcessInitializer;
import br.net.mirante.singular.flow.core.MTransition.ITransitionParametersProcessValidator;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.util.props.MetaDataRef;
import br.net.mirante.singular.flow.util.vars.VarType;

public class FlowBuilderImpl extends
        FlowBuilder<ProcessDefinition<?>, FlowMap, BJava<?>, BPeople<?>, BWait<?>, BEnd<?>, BTransition<?>, BProcessRole<?>, ITaskDefinition> {

    public FlowBuilderImpl(ProcessDefinition<?> processDefinition) {
        super(processDefinition);
    }

    @Override
    protected FlowMap newFlowMap(ProcessDefinition<?> processDefinition) {
        return new FlowMap(processDefinition);
    }

    @Override
    protected BJava<?> newJavaTask(MTaskJava task) {
        return new ImplBJava<>(task);
    }

    @Override
    protected BPeople<?> newPeopleTask(MTaskPeople task) {
        return new ImplBPeople<>(task);
    }

    @Override
    protected BWait<?> newWaitTask(MTaskWait task) {
        return new ImplBWait<>(task);
    }

    @Override
    protected BEnd<?> newEndTask(MTaskEnd task) {
        return new ImplBEnd<>(task);
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

        private final TASK task;

        public ImplBTask(TASK task) {
            this.task = task;
        }

        @Override
        public TASK getTask() {
            return task;
        }
    }

    protected static class ImplBJava<SELF extends ImplBJava<SELF>> extends ImplBTask<SELF, MTaskJava> implements BJava<SELF> {
        public ImplBJava(MTaskJava task) {
            super(task);
        }
    }

    protected static class ImplBPeople<SELF extends ImplBPeople<SELF>> extends ImplBTask<SELF, MTaskPeople> implements BPeople<SELF> {
        public ImplBPeople(MTaskPeople task) {
            super(task);
        }
    }

    protected static class ImplBWait<SELF extends ImplBWait<SELF>> extends ImplBTask<SELF, MTaskWait> implements BWait<SELF> {
        public ImplBWait(MTaskWait task) {
            super(task);
        }
    }

    protected static class ImplBEnd<SELF extends ImplBEnd<SELF>> extends ImplBTask<SELF, MTaskEnd> implements BEnd<SELF> {
        public ImplBEnd(MTaskEnd task) {
            super(task);
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

        public <K extends ProcessInstance> SELF setParametersInitializer(ITransitionParametersProcessInitializer<K> parametrosInicializer) {
            getTransition().setParametersInitializer(parametrosInicializer);
            return self();
        }

        public <K extends ProcessInstance> SELF setParametersValidator(ITransitionParametersProcessValidator<K> parametrosValidator) {
            getTransition().setParametersValidator(parametrosValidator);
            return self();
        }
        
        public <K extends ProcessInstance,T> SELF setMetaDataValue(MetaDataRef<T> propRef, T value) {
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
}
