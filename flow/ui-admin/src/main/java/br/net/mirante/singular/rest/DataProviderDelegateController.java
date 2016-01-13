package br.net.mirante.singular.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.PortletContext;

@RestController
public class DataProviderDelegateController {

    @RequestMapping(value = "/delegate",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> delegate(@RequestBody PortletContext context) {
        return new RestTemplate().postForObject(context.getRestEndpoint(), context, List.class);
    }
}
