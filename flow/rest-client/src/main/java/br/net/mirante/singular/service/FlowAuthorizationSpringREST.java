package br.net.mirante.singular.service;

import static br.net.mirante.singular.flow.core.service.IFlowAuthorizationREST.PATH_PROCESS_DEFINITION_HAS_ACCESS;
import static br.net.mirante.singular.flow.core.service.IFlowAuthorizationREST.PATH_PROCESS_DEFINITION_WITH_ACCESS;
import static br.net.mirante.singular.flow.core.service.IFlowAuthorizationREST.PATH_PROCESS_INSTANCE_HAS_ACCESS;
import static br.net.mirante.singular.flow.core.service.IFlowAuthorizationREST.generateGroupToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.flow.core.MBPMUtil;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.service.IFlowAuthorizationService;

class FlowAuthorizationSpringREST implements IFlowAuthorizationService{

    static final Logger logger = LoggerFactory.getLogger(MBPMUtil.class);
    
    private final String groupToken;
    private final String connectionURL;

    FlowAuthorizationSpringREST(String groupKey, String connectionURL) {
        super();
        this.groupToken = generateGroupToken(groupKey);
        this.connectionURL = connectionURL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> listProcessDefinitionsWithAccess(String userCod, AccessLevel accessLevel) {
        try {
            Set<String> result = new RestTemplate().getForObject(getConnectionURL(PATH_PROCESS_DEFINITION_WITH_ACCESS,
                "userCod","accessLevel"), Set.class,  userCod, accessLevel.name());
            return result;
        } catch (Exception e) {
            logger.error("Erro ao acessar serviço: "+connectionURL+PATH_PROCESS_DEFINITION_WITH_ACCESS, e);
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasAccessToProcessDefinition(String processDefinitionKey, String userCod, AccessLevel accessLevel) {
        try {
            return new RestTemplate().getForObject(getConnectionURL(PATH_PROCESS_DEFINITION_HAS_ACCESS,
                "processDefinitionKey","userCod","accessLevel"), Boolean.class, 
                processDefinitionKey, userCod, accessLevel.name());
        } catch (Exception e) {
            logger.error("Erro ao acessar serviço: "+connectionURL+PATH_PROCESS_DEFINITION_HAS_ACCESS, e);
            return false;
        }
    }

    @Override
    public boolean hasAccessToProcessInstance(String processInstanceFullId, String userCod, AccessLevel accessLevel) {
        try {
            return new RestTemplate().getForObject(getConnectionURL(PATH_PROCESS_INSTANCE_HAS_ACCESS,
                "processInstanceFullId","userCod","accessLevel"), Boolean.class, 
                processInstanceFullId, userCod, accessLevel.name());
        } catch (Exception e) {
            logger.error("Erro ao acessar serviço: "+connectionURL+PATH_PROCESS_INSTANCE_HAS_ACCESS, e);
            return false;
        }
    }
    
    public String getConnectionURL(String path, String...params) {
        return connectionURL + path + "?groupToken="+groupToken + Arrays.stream(params).map(param -> "&"+param+"={"+param+"}").collect(Collectors.joining());
    }
    
}
