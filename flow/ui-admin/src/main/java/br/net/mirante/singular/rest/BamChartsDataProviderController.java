package br.net.mirante.singular.rest;

import static br.net.mirante.singular.flow.core.service.IFlowMetadataREST.generateGroupToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
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
        return uiAdminFacade.retrieveNewInstancesQuantityLastYear(context.getProcessDefinitionCode(),
                getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/meanTimeActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeActiveInstances(context.getProcessDefinitionCode(),
                getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/counterActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> counterActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveCounterActiveInstances(context.getProcessDefinitionCode(),
                getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/statsByActiveTask",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> statsByActiveTask(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveStatsByActiveTask(context.getProcessDefinitionCode());
    }

    @RequestMapping(value = "/meanTimeFinishedInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeFinishedInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeFinishedInstances(context.getProcessDefinitionCode(),
                getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/averageTimesActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> averageTimesActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveAverageTimesActiveInstances(context.getProcessDefinitionCode(),
                getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/meanTimeByTask",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeByTask(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeByTask(resolvePeriodType(context.getQuickFilter()).getPeriod(),
                context.getProcessDefinitionCode());
    }

    @RequestMapping(value = "/meanTimeByProcess",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeByProcess(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeByProcess(resolvePeriodType(context.getQuickFilter()).getPeriod(),
                context.getProcessDefinitionCode(), getProcesseDefinitionsKeysWithAcess());
    }

    @RequestMapping(value = "/endStatusQuantityByPeriod",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> endStatusQuantityByPeriod(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveEndStatusQuantityByPeriod(resolvePeriodType(context.getQuickFilter()).getPeriod(),
                context.getProcessDefinitionCode());
    }

    /**
     * Redirects the REST call comming from the front end
     * to the application that contains the {@link br.net.mirante.singular.flow.core.ProcessDefinition}.
     * This request is forwarded by flow-ui-admin to avoid the browser blocking of
     * cross domain requests (XDR).
     *
     * @param processAbbreviation the process abbreviation
     * @param dashboard the dashboard id
     * @return The chart data
     */
    @RequestMapping(value = "/redirectDataCall/{processAbbreviation}/{dashboard}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> redirectDataCall(@PathVariable String processAbbreviation, @PathVariable String dashboard) {
        GroupDTO groupDTO = flowMetadataFacade.retrieveGroupByProcess(processAbbreviation);
        String url = groupDTO.getConnectionURL() + IFlowMetadataREST.PATH_PROCESS_DASHBOARD_DATA + "?processAbbreviation={processAbbreviation}&dashboard={dashboard}&groupToken={groupToken}";
        try {

            return new RestTemplate().getForObject(url, List.class, processAbbreviation, dashboard, generateGroupToken(groupDTO.getCod()));

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


        private Set<String> getProcesseDefinitionsKeysWithAcess() {
        return flowMetadataFacade.listProcessDefinitionKeysWithAccess(UIAdminSession.get().getUserId(),
                AccessLevel.LIST);
    }

    private PeriodType resolvePeriodType(PortletQuickFilter quickFilter) {
        if (quickFilter != null) {
            return PeriodType.valueOf(quickFilter.getValue());
        }
        return PeriodType.YEARLY;
    }

}
