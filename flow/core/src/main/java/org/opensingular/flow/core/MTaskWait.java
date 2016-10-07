/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.Date;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class MTaskWait extends MTaskUserExecutable<MTaskWait> {

    private final IExecutionDateStrategy<ProcessInstance> executionDateStrategy;

    public MTaskWait(FlowMap flowMap, String name, String abbreviation, IExecutionDateStrategy<?> executionDateStrategy) {
        super(flowMap, name, abbreviation);
        this.executionDateStrategy = (IExecutionDateStrategy<ProcessInstance>) executionDateStrategy;
    }

    public Date getExecutionDate(ProcessInstance instance, TaskInstance taskInstance) {
        return executionDateStrategy.apply(instance, taskInstance);
    }

    public boolean hasExecutionDateStrategy() {
        return executionDateStrategy != null;
    }

    @Override
    public <T extends ProcessInstance> MTaskWait withTargetDate(IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        if(executionDateStrategy != null){
            throw new SingularFlowException("Tarefas agendadas não suportam data alvo.");
        }
        super.withTargetDate(targetDateExecutionStrategy);
        return this;
    }

    @Override
    public boolean isImmediateExecution() {
        return false;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.Wait;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        if (getExecutionPage() != null) {
            Objects.requireNonNull(getAccessStrategy(), "Não foi definida a estrategia de verificação de acesso da tarefa");
        }
        if(getTransitions().size() > 1 && getDefaultTransition() == null && hasExecutionDateStrategy()){
            throw new SingularFlowException(createErrorMsg("A transição default não foi definida"));
        }
    }
}
