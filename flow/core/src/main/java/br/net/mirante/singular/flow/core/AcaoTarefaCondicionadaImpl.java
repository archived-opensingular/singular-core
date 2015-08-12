package br.net.mirante.singular.flow.core;

final class AcaoTarefaCondicionadaImpl extends ConditionalTaskAction {

    private final ITaskPredicate condicao;
    private final TaskAction acao;

    AcaoTarefaCondicionadaImpl(ITaskPredicate condicao, TaskAction acao) {
        this.condicao = condicao;
        this.acao = acao;
    }

    @Override
    public ITaskPredicate getCondition() {
        return condicao;
    }

    @Override
    public void execute(TaskInstance instanciaTarefa) {
        acao.execute(instanciaTarefa);
    }

    @Override
    public String getName() {
        return acao.getName();
    }

    @Override
    public String getCompleteDescription() {
        return acao.getCompleteDescription();
    }

}
