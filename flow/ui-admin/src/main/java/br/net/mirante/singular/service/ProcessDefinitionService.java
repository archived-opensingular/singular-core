package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.dao.MetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;

public interface ProcessDefinitionService {

    IDefinitionDTO retrieveById(Long id);

    List<IDefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc);

    int countAll();

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id);

    int countAll(Long id);

    byte[] retrieveProcessDiagram(String sigla);

    List<MetaDataDTO> retrieveMetaData(Long id);
}
