/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.test.dsl.exemplo;

import org.opensingular.singular.flow.test.dsl.Builder;
import org.opensingular.singular.flow.core.FlowMap;
import org.opensingular.singular.flow.core.MTaskJava;
import org.opensingular.singular.flow.core.MTaskPeople;
import org.opensingular.singular.flow.core.ProcessInstance;

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
