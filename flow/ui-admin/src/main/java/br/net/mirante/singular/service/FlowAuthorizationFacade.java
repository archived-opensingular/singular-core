package br.net.mirante.singular.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import br.net.mirante.singular.dao.DefinitionDAO;
import br.net.mirante.singular.dao.GroupDAO;
import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.GroupDTO;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.service.IFlowAuthorizationService;

@Service
public class FlowAuthorizationFacade {
    
    @Inject
    private GroupDAO groupDAO;

    @Inject
    private DefinitionDAO definitionDAO;
    
    @Cacheable(value = "listProcessDefinitionKeysWithAccess", cacheManager = "cacheManager")
    public Set<String> listProcessDefinitionKeysWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel) {
        return getAuthorizationService(groupDTO).listProcessDefinitionsWithAccess(userCod, accessLevel);
    }
    
    @Cacheable(value = "listProcessDefinitionsWithAccess", cacheManager = "cacheManager")
    public Set<DefinitionDTO> listProcessDefinitionsWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel){
        return listProcessDefinitionKeysWithAccess(groupDTO, userCod, accessLevel).stream().map(definitionDAO::retrieveByKey).collect(Collectors.toSet());
    }
    
    public Set<Long> listProcessDefinitionCodsWithAccess(GroupDTO groupDTO, String userCod, AccessLevel accessLevel){
        return listProcessDefinitionsWithAccess(groupDTO, userCod, accessLevel).stream().map(DefinitionDTO::getCod).collect(Collectors.toSet());
    }

    @Transactional
    public Set<String> listProcessDefinitionKeysWithAccess(String userCod, AccessLevel accessLevel){
        Set<String> cods = new HashSet<>();
        for (GroupDTO groupDTO : groupDAO.retrieveAll()) {
            cods.addAll(listProcessDefinitionsWithAccess(groupDTO, userCod, accessLevel).stream().map(DefinitionDTO::getSigla).collect(Collectors.toSet()));
        }
        return cods;
    }
    
    @Transactional
    @Cacheable(value = "hasAccessToProcessDefinition", cacheManager = "cacheManager")
    public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel){
        DefinitionDTO definitionDTO = definitionDAO.retrieveByKey(processDefinitionKey);
        return hasAccessToProcessDefinition(definitionDTO, userCod, accessLevel);
    }

    @Transactional
    public boolean hasAccessToProcessDefinition(DefinitionDTO definitionDTO, String userCod, AccessLevel accessLevel){
        GroupDTO groupDTO = groupDAO.retrieveById(definitionDTO.getCodGrupo());
        return getAuthorizationService(groupDTO).hasAccessToProcessDefinition(definitionDTO.getSigla(), userCod, accessLevel);
    }
    
    private IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO) {
        try {
            return authorizationService.get(groupDTO);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }
    
    private static LoadingCache<GroupDTO, IFlowAuthorizationService> authorizationService = CacheBuilder.newBuilder().maximumSize(5)
        .build(new CacheLoader<GroupDTO, IFlowAuthorizationService>() {
            @Override
            public IFlowAuthorizationService load(GroupDTO groupDTO) throws Exception {
                return new FlowAuthorizationSpringRest(groupDTO.getCod(), groupDTO.getConnectionURL());
            }
        });
}
