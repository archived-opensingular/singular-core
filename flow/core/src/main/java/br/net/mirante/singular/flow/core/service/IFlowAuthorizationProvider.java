package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.dto.GroupDTO;

public interface IFlowAuthorizationProvider {

    IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO);
}
