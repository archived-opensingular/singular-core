/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.dashboard;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.opensingular.bam.wicket.view.page.processo.MetadadosPage;
import com.opensingular.bam.wicket.view.page.processo.ProcessosPage;
import com.opensingular.bam.wicket.view.template.Content;
import com.opensingular.bam.wicket.view.component.PortletPanel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.opensingular.bam.client.portlet.PortletConfig;
import com.opensingular.bam.client.portlet.PortletSize;
import org.opensingular.singular.flow.core.authorization.AccessLevel;
import org.opensingular.singular.flow.core.dto.GroupDTO;
import org.opensingular.singular.flow.core.dto.IStatusDTO;
import org.opensingular.singular.flow.core.service.IFlowMetadataREST;
import org.opensingular.singular.util.wicket.resource.Color;
import org.opensingular.singular.util.wicket.resource.Icone;

@SuppressWarnings("serial")
public class DashboardContent extends Content {

    static final Logger logger = LoggerFactory.getLogger(DashboardContent.class);

    private String processDefinitionCode;
    private RepeatingView rows;
    private RepeatingView portlets;
    private List<PortletConfig<?>> configs = new ArrayList<>();

    public DashboardContent(String id, String processDefinitionCode) {
        super(id, false, false, false, true);
        this.processDefinitionCode = processDefinitionCode;
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        if (processDefinitionCode == null) {
            return new ResourceModel("label.content.title");
        } else {
            return $m.ofValue(uiAdminFacade.retrieveProcessDefinitionName(processDefinitionCode));
        }
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        if (processDefinitionCode == null) {
            return new ResourceModel("label.content.subtitle");
        } else {
            return $m.ofValue();
        }
    }

    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        RepeatingView breadCrumb = new RepeatingView(id);
        if (processDefinitionCode == null) {
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(ProcessosPage.class, new PageParameters()),
                    getString("breadcrumb.flow.process")));
        } else {
            PageParameters pageParameters = new PageParameters().set(Content.PROCESS_DEFINITION_COD_PARAM, processDefinitionCode);

            breadCrumb.add(createActiveBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(DashboardPage.class, pageParameters).toString(),
                    getString("breadcrumb.statistics")));
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(ProcessosPage.class, pageParameters).toString(),
                    getString("breadcrumb.instances")));
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(MetadadosPage.class, pageParameters).toString(),
                    getString("breadcrumb.metadata")));
            return breadCrumb;
        }
        return breadCrumb;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forUrl("resources/custom/scripts/settings.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    SettingUI.init(); // init settings features\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(rows = new RepeatingView("rows"));
        add(portlets = new RepeatingView("portlets"));

        if (processDefinitionCode == null || flowMetadataFacade.hasAccessToProcessDefinition(processDefinitionCode, getUserId(), AccessLevel.LIST)) {
            Set<String> processCodeWithAccess = flowMetadataFacade.listProcessDefinitionKeysWithAccess(getUserId(), AccessLevel.LIST);
            if (!processCodeWithAccess.isEmpty()) {
                addStatusesPanel(processCodeWithAccess);
                populateConfigs();
                buildDashboard(processCodeWithAccess);
            }
        } else {
            error(getString("error.user.without.access.to.process"));
        }
    }

    protected void buildDashboard(Set<String> processCodeWithAccess) {
        configs.forEach(c -> {
            portlets.add(new PortletPanel<>(portlets.newChildId(), c, processDefinitionCode, configs.indexOf(c)));
        });
        final FeedPanel feed = new FeedPanel("feed", processDefinitionCode, processCodeWithAccess);
        feed.add($b.classAppender(PortletSize.LARGE.getBootstrapSize()));
        portlets.add(feed);
    }

    protected DashboardRow addDashboardRow() {
        DashboardRow dashboardRow = new DashboardRow(rows.newChildId());
        rows.add(dashboardRow);
        return dashboardRow;
    }

    private void addStatusesPanel(Set<String> processCodeWithAccess) {
        IStatusDTO statusDTO = uiAdminFacade.retrieveActiveInstanceStatus(processDefinitionCode, processCodeWithAccess);

        DashboardRow row = addDashboardRow();
        row.addSmallColumn(new StatusPanel("active-instances-status-panel", "label.active.instances.status", statusDTO.getAmount())
                .setIcon(Icone.SPEEDOMETER).setColor(Color.GREEN_SHARP));
        row.addSmallColumn(new StatusPanel("active-average-status-panel", "label.active.average.status",
                statusDTO.getAverageTimeInDays())
                .setUnit(getString("label.active.average.status.unit"))
                .setIcon(Icone.HOURGLASS).setColor(Color.PURPLE_PLUM));
        row.addSmallColumn(new StatusPanel("opened-instances-status-panel", "label.opened.instances.status",
                statusDTO.getOpenedInstancesLast30Days()));
        row.addSmallColumn(new StatusPanel("finished-instances-status-panel", "label.finished.instances.status",
                statusDTO.getFinishedInstancesLast30Days()).setColor(Color.RED_SUNGLO));
    }

    public void populateConfigs() {
        if (processDefinitionCode == null) {
            configs.add(PortletConfigUtil.buildPortletConfigMeanTimeByProcess());
        }

        configs.add(PortletConfigUtil.buildPortletConfigNewInstancesQuantityLastYear());
        configs.add(PortletConfigUtil.buildPortletConfigCounterActiveInstances());

        if (processDefinitionCode != null) {
            configs.add(PortletConfigUtil.buildPortletConfigMeanTimeActiveInstances());
            configs.add(PortletConfigUtil.buildPortletConfigStatsByActiveTask());
            configs.add(PortletConfigUtil.buildPortletConfigMeanTimeByTask());
            configs.add(PortletConfigUtil.buildPortletConfigMeanTimeFinishedInstances());
            configs.add(PortletConfigUtil.buildPortletConfigEndStatusQuantityByPeriod());
            configs.add(PortletConfigUtil.buildPortletConfigAverageTimesActiveInstances());
            configs.add(PortletConfigUtil.buildPortletConfigStatsTimeByActiveTask());

            configs.addAll(getAvailableDashboards(processDefinitionCode));

        }

    }

    public List<PortletConfig<?>> getAvailableDashboards(String processAbbreviation) {
        GroupDTO groupDTO = flowMetadataFacade.retrieveGroupByProcess(processAbbreviation);
        String url = groupDTO.getConnectionURL() + IFlowMetadataREST.PATH_PROCESS_CUSTOM_DASHBOARD + "?processAbbreviation={processAbbreviation}";

        try {
            ResponseEntity<List<PortletConfig<?>>> response =
                    new RestTemplate().exchange(url,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<PortletConfig<?>>>() {
                            },
                            processAbbreviation);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Erro ao acessar serviço: " + url, e);
            return Collections.emptyList();
        }
    }

}

