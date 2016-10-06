/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.rest;

import static org.opensingular.flow.core.service.IFlowMetadataREST.generateGroupToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.opensingular.flow.core.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.opensingular.bam.service.FlowMetadataFacade;
import com.opensingular.bam.client.portlet.PortletContext;
import org.opensingular.flow.core.dto.GroupDTO;
import org.opensingular.flow.core.service.IFlowMetadataREST;

@RestController
public class DataProviderDelegateController {

    static final Logger LOGGER = LoggerFactory.getLogger(DataProviderDelegateController.class);

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    /**
     * Redirects the REST call comming from the front end
     * to the application that contains the {@link ProcessDefinition}.
     * This request is forwarded by flow-ui-admin to avoid the browser blocking of
     * cross domain requests (XDR).
     *
     * @return The chart data
     */
    @RequestMapping(value = "/delegate",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> delegate(@RequestBody PortletContext context) {
        switch (context.getDataEndpoint().getEndpointType()) {
            case LOCAL:
                return getLocalData(context);
            case REST:
                return getRestData(context);
        }
        return Collections.emptyList();
    }

    /**
     * Chamadas para o proprio contexto.
     *
     */
    private List<Map<String, String>> getLocalData(PortletContext context) {
        return new RestTemplate().postForObject(context.getDataEndpoint().getUrl(), context, List.class);
    }

    private List<Map<String, String>> getRestData(PortletContext context) {
        final GroupDTO groupDTO = flowMetadataFacade.retrieveGroupByProcess(context.getProcessDefinitionCode());
        final String url = groupDTO.getConnectionURL()
                + IFlowMetadataREST.PATH_PROCESS_DASHBOARD_DATA
                + "?groupToken={groupToken}";
        try {
            final String token = generateGroupToken(groupDTO.getCod());
            return new RestTemplate().postForObject(url, context, List.class, token);
        } catch (Exception e) {
            LOGGER.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }


}
