package br.net.mirante.singular.rest;


import javax.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.service.FlowMetadataFacade;
import br.net.mirante.singular.service.UIAdminFacade;
import br.net.mirante.singular.view.page.dashboard.PeriodType;
import br.net.mirante.singular.wicket.UIAdminSession;

@RestController
public class BamChartsDataProviderController {

    @Inject
    protected UIAdminFacade uiAdminFacade;

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    @RequestMapping(value = "/newInstancesQuantityLastYear",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> newInstancesQuantityLastYear(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveNewInstancesQuantityLastYear(context.getProcessDefinitionCode(), getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/meanTimeActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeActiveInstances(context.getProcessDefinitionCode(), getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/counterActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> counterActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveCounterActiveInstances(context.getProcessDefinitionCode(), getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/meanTimeByProcess",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeByProcess(@RequestBody PortletContext context) {
        PeriodType pt = PeriodType.YEARLY;
        PortletQuickFilter quickFilter = context.getQuickFilter();
        if (quickFilter != null) {
            pt = PeriodType.valueOf(quickFilter.getValue());
        }
        return uiAdminFacade.retrieveMeanTimeByProcess(pt.getPeriod(), context.getProcessDefinitionCode(), getProcesseDefinitionsKeysWithAcess());
    }

    private Set<String> getProcesseDefinitionsKeysWithAcess() {
        return flowMetadataFacade.listProcessDefinitionKeysWithAccess(UIAdminSession.get().getUserId(), AccessLevel.LIST);
    }

}
