package br.net.mirante.singular.flow.core;


public class MTaskEnd extends MTask<MTaskEnd> {

    public MTaskEnd(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.End;
    }

    @Override
    public boolean canReallocate() {
        return false;
    }
}
