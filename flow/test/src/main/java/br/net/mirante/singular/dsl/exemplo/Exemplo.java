package br.net.mirante.singular.dsl.exemplo;

import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.dsl.Builder;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;
import br.net.mirante.singular.flow.core.ProcessInstance;

public class Exemplo {

    public FlowMap getFlowMap(){
        return new Builder()
                    .task()
                        .java("email")
                            .call(this::enviarEmail)
                            .extraConfig(this::configEmail)
                            .transition()
                                .go()
                        .people("aprovar")
                            .right("diretor")
                            .url("/worklist/tarefa/aprovacaoDiretoria")
                            .extraConfig(this::configAprovacaoDiretoria)
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


    private void configEmail(MTaskJava java){
        //faz o que quiser
    }


    private void configAprovacaoDiretoria(MTaskPeople people){
        //faz o que quiser
    }


    private String enviarEmail(ProcessInstance i){
        return null;
    }

    private String predicate(InstanciaPeticao i){
        return null;
    }
}
