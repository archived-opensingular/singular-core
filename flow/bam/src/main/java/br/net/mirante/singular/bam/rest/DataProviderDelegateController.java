/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.rest;

import javax.inject.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
import static br.net.mirante.singular.flow.core.service.IFlowMetadataREST.generateGroupToken;
import br.net.mirante.singular.bam.service.FlowMetadataFacade;

@RestController
public class DataProviderDelegateController {

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    /**
     * Redirects the REST call comming from the front end
     * to the application that contains the {@link br.net.mirante.singular.flow.core.ProcessDefinition}.
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
            return Collections.emptyList();
        }
    }


}
