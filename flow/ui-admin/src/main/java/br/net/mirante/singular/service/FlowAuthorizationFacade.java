package br.net.mirante.singular.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.DefinitionDAO;
import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.GroupDTO;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.service.IFlowAuthorizationService;

@Service
public class FlowAuthorizationFacade {

    @Inject
    private DefinitionDAO definitionDAO;
    
    @Inject
    private IFlowAuthorizationService authorizationService;
    
    public Set<DefinitionDTO> listProcessDefinitionsWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel){
        return getAuthorizationService(groupDTO).listProcessDefinitionsWithAccess(userCod, accessLevel).stream().map(definitionDAO::retrieveByKey).collect(Collectors.toSet());
    }
    
    //TODO - acessar o serviço REST da aplicação através do GroupDTO.getConnectionURL()
    private IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO) {
        return authorizationService;
    }
}
