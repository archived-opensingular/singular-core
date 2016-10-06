/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.opensingular.bam.support.persistence.dao.DefinitionDAO;
import com.opensingular.bam.support.persistence.dto.DefinitionDTO;
import com.opensingular.bam.support.persistence.dto.MetaDataDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import com.opensingular.bam.dao.InstanceDAO;
import com.opensingular.bam.dto.InstanceDTO;

@Service("processDefinitionService")
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Inject
    private DefinitionDAO definitionDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Override
    @Transactional
    public DefinitionDTO retrieveById(Integer processDefinitionCod) {
        return definitionDAO.retrieveById(processDefinitionCod);
    }

    @Cacheable(value = "retrieveProcessDefinitionByKey", cacheManager = "cacheManager")
    @Override
    @Transactional
    public DefinitionDTO retrieveByKey(String processDefinitionKey) {
        return definitionDAO.retrieveByKey(processDefinitionKey);
    }
    
    @Override
    @Transactional
    public List<DefinitionDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess) {
        return definitionDAO.retrieveAll(first, size, orderByProperty, asc, processCodeWithAccess);
    }

    @Override
    @Transactional
    public int countAll(Set<String> processCodeWithAccess) {
        return definitionDAO.countAll(processCodeWithAccess);
    }

    @Override
    @Transactional
    public List<InstanceDTO> retrieveAll(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod) {
        return instanceDAO.retrieveAll(first, size, orderByProperty, asc, processDefinitionCod);
    }

    @Override
    @Transactional
    public int countAll(Integer processDefinitionCod) {
        return instanceDAO.countAll(processDefinitionCod);
    }

    @Override
    @Transactional
    public List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod) {
        return definitionDAO.retrieveMetaData(processDefinitionCod);
    }

}
