/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

import org.opensingular.singular.flow.schedule.IScheduleData;

@SuppressWarnings("unchecked")
public class MTaskJava extends MTask<MTaskJava> {

    private IScheduleData scheduleData;

    private ImplTaskJava taskImpl;

    @SuppressWarnings("rawtypes")
    private ImplTaskBlock blockImpl;

    public MTaskJava(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.Java;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }

    public boolean isCalledInBlock() {
        return blockImpl != null;
    }

    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    public <T extends ProcessInstance> MTaskJava callBlock(ImplTaskBlock<T> implBloco, IScheduleData scheduleData) {
        Objects.requireNonNull(implBloco);
        Objects.requireNonNull(scheduleData);
        this.blockImpl = implBloco;
        this.scheduleData = scheduleData;
        return this;
    }

    public MTaskJava call(ImplTaskJava impl) {
        Objects.requireNonNull(impl);
        taskImpl = impl;
        return this;
    }

    @Override
    public boolean isImmediateExecution() {
        return getScheduleData() == null;
    }

    @Override
    public void execute(ExecutionContext execucaoTask) {
        if (taskImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Não foi configurado o código de execução da tarefa"));
        }
        Object result = taskImpl.call(execucaoTask);
        if (result instanceof String) {
            execucaoTask.setTransition((String) result);
        }
    }

    public Object executarByBloco(Collection<? extends ProcessInstance> instancias) {
        if (blockImpl == null) {
            throw new SingularFlowException(createErrorMsg("Chamada inválida. Não se aplica execução em bloco nesta tarefa."));
        }
        Object result = blockImpl.call(instancias);

        if (result == null) {
            long qtdAlterado = instancias.stream().filter(i -> !equals(i.getEstado())).count();
            result = "De " + instancias.size() + " instancias no estado [" + getCompleteName() + "], " + qtdAlterado + " mudaram de estado";
        }
        return result;
    }

    @FunctionalInterface
    public interface ImplTaskJava extends Serializable {
        Object call(ExecutionContext execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskBlock<K extends ProcessInstance> {
        Object call(Collection<K> instanciasProcesso);
    }
}
