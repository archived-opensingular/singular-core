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
                        .java("EMAIL")
                            .call(this::enviarEmail)
                            .extraConfig(this::configEmail)
                            .transition()
                                .go()
                        .people("APROVAR")
                            .right("diretor")
                            .url("/worklist/tarefa/aprovacaoDiretoria")
                            .extraConfig(this::configAprovacaoDiretoria)
                            .transition("aprovado")
                                .gotTo("AGUARDAR_PAGAMENTO")
                            .transition("rejeitado")
                                .gotTo("REVISAR")
                        .wait("AGUARDAR_PAGAMENTO")
                            .until(this::predicate)
                            .transition()
                                .go()
                        .end("FIM")
                        .people("REVISAR")
                            .right("diretor")
                            .url("/worklist/tarefa/revisaoParecer")
                            .transition("reconsiderar")
                                 .goTo("EMAIL")
                            .transition("aprovar.parecer")
                                .goTo("FIM")
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

    private String predicate(ProcessInstance i){
        return null;
    }
}
