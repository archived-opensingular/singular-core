package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.Objects;

import com.google.common.base.Preconditions;

@SuppressWarnings("unchecked")
public class MTaskWait extends MTaskUserExecutable<MTaskWait> {

    private final IExecutionDateStrategy<ProcessInstance> executionDateStrategy;

    public MTaskWait(FlowMap flowMap, String name, IExecutionDateStrategy<?> executionDateStrategy) {
        super(flowMap, name);
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
        Preconditions.checkArgument(executionDateStrategy == null, "Tarefas agendadas não suportam data alvo.");
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
    public TaskType getTaskType() {
        return TaskType.Wait;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        if (getExecutionPage() != null) {
            Objects.requireNonNull(getAccessStrategy(), "Não foi definida a estrategia de verificação de acesso da tarefa");
        }
    }
}
