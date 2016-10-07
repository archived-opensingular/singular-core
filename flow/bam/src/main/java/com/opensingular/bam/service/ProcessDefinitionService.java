/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;
import java.util.Set;

import com.opensingular.bam.dto.InstanceDTO;
import com.opensingular.bam.support.persistence.dto.DefinitionDTO;
import com.opensingular.bam.support.persistence.dto.MetaDataDTO;

public interface ProcessDefinitionService {

    DefinitionDTO retrieveById(Integer processDefinitionCod);

    DefinitionDTO retrieveByKey(String processDefinitionKey);

    List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess);

    int countAll(Set<String> processCodeWithAccess);

    List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod);

    int countAll(Integer processDefinitionCod);

    List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod);
}
