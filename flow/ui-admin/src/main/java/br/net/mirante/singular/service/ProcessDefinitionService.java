package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.InstanceDTO;
import br.net.mirante.singular.dto.MetaDataDTO;

public interface ProcessDefinitionService {

    DefinitionDTO retrieveById(Integer processDefinitionCod);

    List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc);

    int countAll();

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod);

    int countAll(Integer processDefinitionCod);

    byte[] retrieveProcessDiagramFromRestURL(String sigla);

    byte[] retrieveProcessDiagram(String sigla);

    List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod);
}
