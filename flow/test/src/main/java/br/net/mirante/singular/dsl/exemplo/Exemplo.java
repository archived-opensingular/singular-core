package br.net.mirante.singular.dsl.exemplo;

import br.net.mirante.singular.InstanciaDefinicao;
import br.net.mirante.singular.dsl.Builder;
import br.net.mirante.singular.flow.core.FlowMap;

public class Exemplo {

    public FlowMap getFlowMap(){
        return new Builder()
                    .task()
                        .java("email")
                            .call(this::enviarEmail)
                            .transition()
                                .go()
                        .people("aprovar")
                            .right("diretor")
                            .url("/worklist/tarefa/aprovacaoDiretoria")
                            .transition("aprovado")
                                .gotTo("aguardar.pagamento")
                            .transition("rejeitado")
                                .gotTo("revisar")
                        .wait("aguardar.pagamento")
                            .until(this::predicate)
                            .transition()
                                .go()
                        .end("fim")
                        .people("revisar")
                            .right("diretor")
                            .url("/worklist/tarefa/revisaoParecer")
                            .transition("reconsiderar")
                                .goTo("email")
                            .transition("aprovar.parecer")
                                .goTo("fim")
                        .build();
    }



    private String enviarEmail(InstanciaDefinicao i){
        return null;
    }

    private String predicate(InstanciaDefinicao i){
        return null;
    }
}
