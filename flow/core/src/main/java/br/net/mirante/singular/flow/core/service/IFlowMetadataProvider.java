package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.dto.GroupDTO;

public interface IFlowMetadataProvider {

    IFlowMetadataService getMetadataService(GroupDTO groupDTO);
}
