package br.net.mirante.singular.flow.core.service;

import java.util.List;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.flow.core.dto.IInstanceDTO;

public interface IUIAdminService<DEFINITION extends IDefinitionDTO, INSTANCE extends IInstanceDTO> {

    DEFINITION retrieveDefinitionById(Long id);

    List<DEFINITION> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc);

    int countAllDefinition();

    List<INSTANCE> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Long id);

    int countAllInstance(Long id);

    byte[] retrieveProcessDiagram(String sigla);
}
