/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.util;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;


import br.net.mirante.singular.bam.support.persistence.dao.DefinitionDAO;
import br.net.mirante.singular.bam.support.persistence.dto.DefinitionDTO;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowMetadataProvider;
import br.net.mirante.singular.flow.core.service.IFlowMetadataService;

public class FlowMetadataProviderMock implements IFlowMetadataProvider {

    @Inject
    private DefinitionDAO definitionDAO;
    
    private IFlowMetadataService flowMetadataService = new IFlowMetadataService() {
        
        @Override
        public byte[] processDefinitionDiagram(String processDefinitionKey) {
            return null;
        }
        
        @Override
        public Set<String> listProcessDefinitionsWithAccess(String userCod, AccessLevel accessLevel) {
            return definitionDAO.retrieveAll().stream().map(DefinitionDTO::getSigla).collect(Collectors.toSet());
        }
        
        @Override
        public boolean hasAccessToProcessInstance(String processInstanceFullId, String userCod, AccessLevel accessLevel) {
            return true;
        }
        
        @Override
        public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel) {
            return true;
        }
    };
    
    @Override
    public IFlowMetadataService getMetadataService(GroupDTO groupDTO) {
        return flowMetadataService;
    }
}
