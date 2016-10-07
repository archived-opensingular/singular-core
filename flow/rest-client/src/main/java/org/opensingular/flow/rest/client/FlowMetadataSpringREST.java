/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.rest.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.opensingular.flow.core.MBPMUtil;
import org.opensingular.flow.core.authorization.AccessLevel;
import org.opensingular.flow.core.service.IFlowMetadataService;
import static org.opensingular.flow.core.service.IFlowMetadataREST.*;

class FlowMetadataSpringREST implements IFlowMetadataService {

    static final Logger logger = LoggerFactory.getLogger(MBPMUtil.class);
    
    private final String groupToken;
    private final String connectionURL;

    FlowMetadataSpringREST(String groupKey, String connectionURL) {
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
    
    @Override
    public byte[] processDefinitionDiagram(String processDefinitionKey) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());    
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.IMAGE_PNG));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(getConnectionURL(PATH_PROCESS_DEFINITION_DIAGRAM,
                "processDefinitionKey"), HttpMethod.GET, entity, byte[].class, processDefinitionKey);

            if(response.getStatusCode().equals(HttpStatus.OK)){       
                return response.getBody();
            }
            logger.error("Erro ao acessar serviço: "+connectionURL+PATH_PROCESS_DEFINITION_DIAGRAM+": StatusCode: "+response.getStatusCode());
        } catch (Exception e) {
            logger.error("Erro ao acessar serviço: "+connectionURL+PATH_PROCESS_DEFINITION_DIAGRAM, e);
        }
        return null;
    }

    public String getConnectionURL(String path, String... params) {
        return connectionURL + path + "?groupToken=" + groupToken + addOtherParameters(params);
    }

    public String addOtherParameters(String... params){
        if(params != null) {
            return Arrays
                    .stream(params)
                    .map(param -> "&" + param + "={" + param + "}")
                            .collect(Collectors.joining());
        } else {
            return null;
        }
    }
    
}
