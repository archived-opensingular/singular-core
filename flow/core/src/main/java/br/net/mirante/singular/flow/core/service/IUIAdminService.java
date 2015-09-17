package br.net.mirante.singular.flow.core.service;

import java.util.List;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;

public interface IUIAdminService<DEFINITION extends IDefinitionDTO> {

    DEFINITION retrieveById(Long id);

    List<DEFINITION> retrieveAll(int first, int size, String orderByProperty, boolean asc);
}
