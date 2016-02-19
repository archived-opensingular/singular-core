package br.net.mirante.singular.service;

import java.util.List;
import java.util.Set;

import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.InstanceDTO;
import br.net.mirante.singular.dto.MetaDataDTO;

public interface ProcessDefinitionService {

    DefinitionDTO retrieveById(Integer processDefinitionCod);

    DefinitionDTO retrieveByKey(String processDefinitionKey);

    List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess);

    int countAll(Set<String> processCodeWithAccess);

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod);

    int countAll(Integer processDefinitionCod);

    List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod);
}
