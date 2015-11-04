package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.dao.DefinitionDTO;
import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.dao.MetaDataDTO;

public interface ProcessDefinitionService {

    DefinitionDTO retrieveById(Long id);

    List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc);

    int countAll();

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Long id);

    int countAll(Long id);

    byte[] retrieveProcessDiagramFromRestURL(String sigla);

    byte[] retrieveProcessDiagram(String sigla);

    List<MetaDataDTO> retrieveMetaData(Long id);
}
