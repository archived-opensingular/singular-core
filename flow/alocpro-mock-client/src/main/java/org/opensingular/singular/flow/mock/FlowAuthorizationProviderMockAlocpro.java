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

package org.opensingular.singular.flow.mock;

import org.opensingular.flow.core.authorization.AccessLevel;
import org.opensingular.flow.core.dto.GroupDTO;
import org.opensingular.flow.core.service.IFlowAuthorizationProvider;
import org.opensingular.flow.core.service.IFlowAuthorizationService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class FlowAuthorizationProviderMockAlocpro implements IFlowAuthorizationProvider {

    @Override
    public IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO) {
        return new IFlowAuthorizationService() {

            @Override
            public Set<String> listProcessDefinitionsWithAccess(String userCod, AccessLevel accessLevel) {
                return new HashSet<>(Arrays.asList(new String[]{
                        "DesbloquearDia",
                        "AnalisarReembolsosDeslocamento",
                        "MarcarFeriasDefinicao",
                        "AcordarTermos",
                        "Faturar",
                        "ReembolsoAvulso",
                        "ReembolsarDeslocamento",
                        "Pendencia",
                        "NegociacaoRh",
                        "Chamado",
                        "AcompanharContratado",
                        "Admissao",
                        "Contratacao",
                        "AberturaOS",
                        "Desligamento",
                        "GestaoRisco",
                        "OsCni",
                        "AcordoSaldoMes",
                        "AgendarFerias",
                        "Selecao",
                        "EncaminharCandidatoSicoob",
                        "SolicitacaoSustentacaoCniDefinicao",
                        "SolicitacaoSustentacaoSefDefinicao",
                        "SolicitacaoSustentacao",
                        "EncaminharCandidatoMirante",
                        "LiberarLancamentoAtv",
                        "PrevisaoFluxoCaixa",
                        "EmissaoNFContrato"
                }));
            }

            @Override
            public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel) {
                return true;
            }

            @Override
            public boolean hasAccessToProcessInstance(String processInstanceFullId, String userCod, AccessLevel accessLevel) {
                return true;
            }

        };
    }
}
