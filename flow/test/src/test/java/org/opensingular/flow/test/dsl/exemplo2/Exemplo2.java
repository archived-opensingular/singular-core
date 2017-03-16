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

package org.opensingular.flow.test.dsl.exemplo2;

import org.opensingular.flow.test.dsl.exemplo2.dsl.Builder2;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.MTask;

public class Exemplo2 {

    public FlowMap getFlowMap() {
        // @formatter:off
        return new Builder2()
                .javaTask("ENVIAR_EMAIL")
                    .config(this::configEmail)
                    .transition()
                .peopleTask("APROVAR_PARECER")
                    .config(this::configAprovar)
                    .transitionTo("AGUARDAR_PAGAMENTO", "aprovado")
                    .transitionTo("REVISAR", "rejeitado")
                .waitTask("AGUARDAR_PAGAMENTO")
                    .config(this::configAguardarPagamento)
                    .transition()
                .endTask("FIM")
                .peopleTask("REVISAR")
                    .config(this::configRevisar)
                    .transitionTo("EMAIL", "reconsiderar")
                    .transition("aprovar.parecer")
                .build();
        // @formatter:on
    }

    private void configAguardarPagamento(MTask mTask) {

    }

    private void configEmail(MTask java) {
        //faz o que quiser
    }


    private void configAprovar(MTask java) {
        //faz o que quiser
    }

    private void configRevisar(MTask java) {
        //faz o que quiser
    }


}
