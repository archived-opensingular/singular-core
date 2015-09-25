package br.net.mirante.singular.flow.core;

import java.util.Objects;

public class MTaskPeople extends MTaskUserExecutable<MTaskPeople> {

    private EstrategiaAlertaTarefa estrategiaAlerta;

    private boolean podeRealocar = true;

    public MTaskPeople(FlowMap mapa, String nome, String abbreviation) {
        super(mapa, nome, abbreviation);
    }

    @Override
    public IEntityTaskType getTaskType() {
        return TaskType.People;
    }

    @Override
    public boolean canReallocate() {
        return podeRealocar;
    }

    public MTaskPeople setPodeRealocar(boolean podeRealocar) {
        this.podeRealocar = podeRealocar;
        return this;
    }

    public MTaskPeople setAlerta(EstrategiaAlertaTarefa estrategiaAlerta) {
        this.estrategiaAlerta = estrategiaAlerta;
        return this;
    }

    public EstrategiaAlertaTarefa getAlerta() {
        return estrategiaAlerta;
    }

    @Override
    void verifyConsistency() {
        super.verifyConsistency();
        Objects.requireNonNull(getExecutionPage(), "Não foi definida a estratégia da página para execução da tarefa.");
        Objects.requireNonNull(getAccessStrategy(), "Não foi definida a estrategia de verificação de acesso da tarefa");
    }
}
