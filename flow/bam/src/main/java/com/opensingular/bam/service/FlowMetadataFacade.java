/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.opensingular.bam.dao.GroupDAO;
import com.opensingular.bam.support.persistence.dao.DefinitionDAO;
import com.opensingular.bam.support.persistence.dto.DefinitionDTO;
import org.opensingular.singular.flow.core.authorization.AccessLevel;
import org.opensingular.singular.flow.core.dto.GroupDTO;
import org.opensingular.singular.flow.core.service.IFlowMetadataProvider;
import org.opensingular.singular.persistence.entity.Dashboard;
import org.opensingular.singular.persistence.entity.Portlet;

@Service
public class FlowMetadataFacade {

    @Inject
    private GroupDAO groupDAO;

    @Inject
    private DefinitionDAO definitionDAO;

    @Inject
    private IFlowMetadataProvider flowMetadataProvider;

    @Transactional
    public byte[] processDefinitionDiagram(DefinitionDTO definitionDTO) {
        return flowMetadataProvider.getMetadataService(retrieveGroup(definitionDTO.getCodGrupo())).processDefinitionDiagram(definitionDTO.getSigla());
    }

    @Cacheable(value = "retrieveGroup", cacheManager = "cacheManager")
    @Transactional
    public GroupDTO retrieveGroup(String codGrupo) {
        return groupDAO.retrieveById(codGrupo);
    }

    @Cacheable(value = "listProcessDefinitionKeysWithAccess", cacheManager = "cacheManager")
    public Set<String> listProcessDefinitionKeysWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel) {
        return flowMetadataProvider.getMetadataService(groupDTO).listProcessDefinitionsWithAccess(userCod, accessLevel);
    }

    @Cacheable(value = "listProcessDefinitionsWithAccess", cacheManager = "cacheManager")
    public Set<DefinitionDTO> listProcessDefinitionsWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel) {
        return listProcessDefinitionKeysWithAccess(groupDTO, userCod, accessLevel).stream().map(definitionDAO::retrieveByKey).filter(def -> def != null).collect(Collectors.toSet());
    }

    public Set<Integer> listProcessDefinitionCodsWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel) {
        return listProcessDefinitionsWithAccess(groupDTO, userCod, accessLevel).stream().map(DefinitionDTO::getCod).collect(Collectors.toSet());
    }

    @Transactional
    public Set<String> listProcessDefinitionKeysWithAccess(String userCod, AccessLevel accessLevel) {
        Set<String> cods = new HashSet<>();
        for (GroupDTO groupDTO : groupDAO.retrieveAll()) {
            cods.addAll(listProcessDefinitionsWithAccess(groupDTO, userCod, accessLevel).stream().map(DefinitionDTO::getSigla).collect(Collectors.toSet()));
        }
        return cods;
    }

    @Transactional
    @Cacheable(value = "hasAccessToProcessDefinition", cacheManager = "cacheManager")
    public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel) {
        DefinitionDTO definitionDTO = definitionDAO.retrieveByKey(processDefinitionKey);
        if (definitionDTO == null) {
            return false;
        }

        return hasAccessToProcessDefinition(definitionDTO, userCod, accessLevel);
    }

    @Transactional
    public boolean hasAccessToProcessDefinition(DefinitionDTO definitionDTO, String userCod, AccessLevel accessLevel) {
        GroupDTO groupDTO = retrieveGroup(definitionDTO.getCodGrupo());
        return flowMetadataProvider.getMetadataService(groupDTO).hasAccessToProcessDefinition(definitionDTO.getSigla(), userCod, accessLevel);
    }

    @Transactional
    @Cacheable(value = "retrieveGroupByProcess", cacheManager = "cacheManager")
    public GroupDTO retrieveGroupByProcess(String processDefinitionKey) {
        DefinitionDTO definitionDTO = definitionDAO.retrieveByKey(processDefinitionKey);
        return retrieveGroup(definitionDTO.getCodGrupo());
    }

    @Transactional
    public List<Portlet> getAuthorizedPortlets(Dashboard dashboard, String userId) {
        return dashboard.getPortlets().stream()
                .filter(portlet -> portlet.getProcessAbbreviation() == null || hasAccessToProcessDefinition(portlet.getProcessAbbreviation(), userId,  AccessLevel.LIST))
                .collect(Collectors.toList());
    }
}
