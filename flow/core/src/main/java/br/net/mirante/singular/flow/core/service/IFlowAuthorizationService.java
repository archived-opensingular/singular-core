package br.net.mirante.singular.flow.core.service;

import java.util.Set;

import javax.validation.constraints.NotNull;

import br.net.mirante.singular.flow.core.authorization.AccessLevel;

public interface IFlowAuthorizationService {

    Set<String> listProcessDefinitionsWithAccess(@NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessDefinition(@NotNull String processDefinitionKey, @NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessInstance(@NotNull String processInstanceFullId, @NotNull String userCod, @NotNull AccessLevel accessLevel);

}
