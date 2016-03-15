/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.service;

import java.util.List;
import java.util.Set;

import br.net.mirante.singular.bam.dto.DefinitionDTO;
import br.net.mirante.singular.bam.dto.InstanceDTO;
import br.net.mirante.singular.bam.dto.MetaDataDTO;

public interface ProcessDefinitionService {

    DefinitionDTO retrieveById(Integer processDefinitionCod);

    DefinitionDTO retrieveByKey(String processDefinitionKey);

    List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess);

    int countAll(Set<String> processCodeWithAccess);

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod);

    int countAll(Integer processDefinitionCod);

    List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod);
}
