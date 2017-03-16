/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
