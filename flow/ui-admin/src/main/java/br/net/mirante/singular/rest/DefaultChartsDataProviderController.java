/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.rest;

import javax.inject.Inject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.service.UIAdminFacade;
import br.net.mirante.singular.view.page.dashboard.PeriodType;

@RestController
public class DefaultChartsDataProviderController {

    @Inject
    protected UIAdminFacade uiAdminFacade;

    @RequestMapping(value = "/newInstancesQuantityLastYear",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> newInstancesQuantityLastYear(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveNewInstancesQuantityLastYear(context.getProcessDefinitionCode(),
                context.getProcessDefinitionKeysWithAccess());
    }

    @RequestMapping(value = "/meanTimeActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> meanTimeActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveMeanTimeActiveInstances(context.getProcessDefinitionCode(),
                context.getProcessDefinitionKeysWithAccess());
    }

    @RequestMapping(value = "/counterActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> counterActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveCounterActiveInstances(context.getProcessDefinitionCode(),
                context.getProcessDefinitionKeysWithAccess());
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
                context.getProcessDefinitionKeysWithAccess());
    }

    @RequestMapping(value = "/averageTimesActiveInstances",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> averageTimesActiveInstances(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveAverageTimesActiveInstances(context.getProcessDefinitionCode(),
                context.getProcessDefinitionKeysWithAccess());
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
                context.getProcessDefinitionCode(), context.getProcessDefinitionKeysWithAccess());
    }

    @RequestMapping(value = "/endStatusQuantityByPeriod",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> endStatusQuantityByPeriod(@RequestBody PortletContext context) {
        return uiAdminFacade.retrieveEndStatusQuantityByPeriod(resolvePeriodType(context.getQuickFilter()).getPeriod(),
                context.getProcessDefinitionCode());
    }

    private PeriodType resolvePeriodType(PortletQuickFilter quickFilter) {
        if (quickFilter != null) {
            return PeriodType.valueOf(quickFilter.getValue());
        }
        return PeriodType.YEARLY;
    }

    public static void main(String[] args) throws ParseException {
        Date date1 = DateUtils.parseDate("2016-01-02 23:23:12","YYYY-MM-DD HH:mm:ss");
        Date date2 = new Date();

    }

}
