package br.net.mirante.singular.flow.core;


public class MTaskEnd extends MTask<MTaskEnd> {

    public MTaskEnd(FlowMap mapa, String nome) {
        super(mapa, nome);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.End;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }
}
