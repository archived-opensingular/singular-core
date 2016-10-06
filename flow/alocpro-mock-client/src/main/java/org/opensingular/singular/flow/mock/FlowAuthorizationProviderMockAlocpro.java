/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.mock;

import org.opensingular.singular.flow.core.authorization.AccessLevel;
import org.opensingular.singular.flow.core.dto.GroupDTO;
import org.opensingular.singular.flow.core.service.IFlowAuthorizationProvider;
import org.opensingular.singular.flow.core.service.IFlowAuthorizationService;
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
