package br.net.mirante.singular.flow.core;

public class AcoesTarefa {

    public static ConditionalTaskAction transitar(final TaskPredicate condicao, final MTransition transicao) {
        return transitar(condicao, transicao.getName());
    }

    public static ConditionalTaskAction transitar(final TaskPredicate condicao, final String nomeDestino) {
        return new ConditionalTaskAction() {

            @Override
            public TaskPredicate getCondition() {
                return condicao;
            }

            @Override
            public void execute(TaskInstance instanciaTarefa) {
                instanciaTarefa.log("Transição Automática", "motivo: " + condicao.getDescription(instanciaTarefa));
                instanciaTarefa.getProcessInstance().executarTransicao(nomeDestino);

            }

            @Override
            public String getName() {
                return "Executar Transicao";
            }

            @Override
            public String getCompleteDescription() {
                return getName() + " '" + nomeDestino + "'";
            }

        };

    }

}
