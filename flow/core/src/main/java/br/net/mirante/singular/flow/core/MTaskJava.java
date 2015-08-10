package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.Collection;

import com.google.common.base.Preconditions;

import br.net.mirante.singular.flow.schedule.IScheduleData;

@SuppressWarnings("unchecked")
public class MTaskJava extends MTask<MTaskJava> {

    private IScheduleData scheduleData;

    private ImplTaskJava taskImpl;

    @SuppressWarnings("rawtypes")
    private ImplTaskBlock blockImpl;

    public MTaskJava(FlowMap mapa, String nome) {
        super(mapa, nome);
    }

    @Override
    public TaskType getTaskType() {
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
        Preconditions.checkNotNull(implBloco);
        Preconditions.checkNotNull(scheduleData);
        this.blockImpl = implBloco;
        this.scheduleData = scheduleData;
        return this;
    }

    public MTaskJava call(ImplTaskJava impl) {
        Preconditions.checkNotNull(impl);
        taskImpl = impl;
        return this;
    }

    @Override
    public boolean isImmediateExecution() {
        return getScheduleData() == null;
    }

    @Override
    public void execute(ExecucaoMTask execucaoTask) {
        if (taskImpl == null) {
            throw generateError("Chamada inválida. Não foi configurado o código de execução da tarefa");
        }
        Object result = taskImpl.call(execucaoTask);
        if (result instanceof String) {
            execucaoTask.setTransicaoResultado((String) result);
        }
    }

    public Object executarByBloco(Collection<? extends ProcessInstance> instancias) {
        if (blockImpl == null) {
            throw generateError("Chamada inválida. Não se aplica execução em bloco nesta tarefa.");
        }
        Object result = blockImpl.call(instancias);

        if (result == null) {
            /* XXX: O método "equals" foi implementado em MTask. Ele não deveria ser usado aqui? */
            long qtdAlterado = instancias.stream().filter(i -> i.getEstado() != this).count();
            result = "De " + instancias.size() + " instancias no estado [" + getCompleteName() + "], " + qtdAlterado + " mudaram de estado";
        }
        return result;
    }

    @FunctionalInterface
    public interface ImplTaskJava extends Serializable {
        Object call(ExecucaoMTask execucaoTask);
    }

    @FunctionalInterface
    public interface ImplTaskBlock<K extends ProcessInstance> {
        Object call(Collection<K> instanciasProcesso);
    }
}
