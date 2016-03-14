/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.view.page.dashboard;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
import br.net.mirante.singular.persistence.entity.Dashboard;
import br.net.mirante.singular.persistence.entity.Portlet;
import br.net.mirante.singular.view.component.PortletPanel;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class CustomDashboardContent extends Content {

    static final Logger logger = LoggerFactory.getLogger(CustomDashboardContent.class);

    private String customDashboardCode;
    private RepeatingView portlets;
    private Dashboard dashboard;

    public CustomDashboardContent(String id, String customDashboardCode) {
        super(id, false, false, false, true);
        this.customDashboardCode = customDashboardCode;
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        if (dashboard != null) {
            return $m.property(dashboard, "name");
        } else {
            return new ResourceModel("label.content.title");
        }
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("");
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
        add(portlets = new RepeatingView("portlets"));

        dashboard = uiAdminFacade.retrieveDashboardById(customDashboardCode);

        List<Portlet> authorizedPortlets = flowMetadataFacade.getAuthorizedPortlets(dashboard, getUserId());

        if (!authorizedPortlets.isEmpty()) {
            Set<String> processCodeWithAccess = flowMetadataFacade.listProcessDefinitionKeysWithAccess(getUserId(), AccessLevel.LIST);
            if (!processCodeWithAccess.isEmpty()) {
                for (Portlet portlet : authorizedPortlets) {
                    PortletConfig<?> config = buildConfig(portlet);
                    buildDashboard(portlet, config);
                }
            }

        } else {
            error(getString("error.user.without.access.to.process"));
        }

        super.onInitialize();
    }

    protected void buildDashboard(Portlet portlet, PortletConfig<?> config) {
        String footer = null;
        if (portlet.getProcessAbbreviation() != null) {
            footer = uiAdminFacade.retrieveProcessDefinitionName(portlet.getProcessAbbreviation());
        }

        portlets.add(new PortletPanel<>(portlets.newChildId(), config,
                portlet.getProcessAbbreviation(), portlet.getOrdem().intValue(),
                footer));
    }

    private PortletConfig<?> buildConfig(Portlet portlet) {
        if (portlet.isDynamic()) {
            GroupDTO groupDTO = flowMetadataFacade.retrieveGroupByProcess(portlet.getProcessAbbreviation());
            String url = groupDTO.getConnectionURL() + IFlowMetadataREST.PATH_PROCESS_DETAIL_DASHBOARD + "?processAbbreviation={processAbbreviation}&dashboardViewName={dashboardViewName}";

            try {
                PortletConfig config = new RestTemplate().getForObject(url,
                        PortletConfig.class,
                        portlet.getProcessAbbreviation(), portlet.getName());
                if (config == null) {
                    throw new SingularException("Configuração não encontrada para o portlet.");
                }
                return config;
            } catch (Exception e) {
                throw new SingularException(String.format("Erro ao acessar serviço: %s usando processo: %s e dashboard %s ",
                        url, portlet.getProcessAbbreviation(), portlet.getName()), e);
            }
        } else {
            return PortletConfigUtil.getById(portlet.getName());
        }
    }

}

