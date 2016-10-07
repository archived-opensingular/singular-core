/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.test.dsl.exemplo;

import org.opensingular.flow.test.dsl.Builder;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.MTaskJava;
import org.opensingular.flow.core.MTaskPeople;
import org.opensingular.flow.core.ProcessInstance;

public class Exemplo {

    public FlowMap getFlowMap() {
        // @formatter:off
        return new Builder()
                    .task()
                        .java("EMAIL")
                            .call(this::enviarEmail)
                            .transition()
                                .to()
                        .people("APROVAR")
                            .right("diretor")
                            .url("/worklist/tarefa/aprovacaoDiretoria")
                            .extraConfig(this::configAprovacaoDiretoria)
                            .transition("aprovado")
                                .to("AGUARDAR_PAGAMENTO")
                            .transition("rejeitado")
                                .to("REVISAR")
                        .wait("AGUARDAR_PAGAMENTO")
                            .until(this::predicate)
                            .transition()
                                .to()
                        .end("FIM")
                        .people("REVISAR")
                            .right("diretor")
                            .url("/worklist/tarefa/revisaoParecer")
                            .transition("reconsiderar")
                                 .to("EMAIL")
                            .transition("aprovar.parecer")
                                .to("FIM")
                        .build();
            // @formatter:on
    }


    private void configEmail(MTaskJava java) {
        //faz o que quiser
    }


    private void configAprovacaoDiretoria(MTaskPeople people) {
        //faz o que quiser
    }


    private void enviarEmail(Object ...o ) {
        return;
    }

    private String predicate(ProcessInstance i) {
        return null;
    }
}
