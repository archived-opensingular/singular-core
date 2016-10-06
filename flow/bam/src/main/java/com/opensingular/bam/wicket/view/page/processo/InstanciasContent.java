/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.processo;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;

import javax.inject.Inject;

import com.opensingular.bam.service.UIAdminFacade;
import com.opensingular.bam.wicket.view.SingularWicketContainer;
import com.opensingular.bam.wicket.view.page.dashboard.DashboardPage;
import com.opensingular.bam.wicket.view.template.Content;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import org.opensingular.singular.flow.core.authorization.AccessLevel;
import org.opensingular.singular.flow.core.dto.IDefinitionDTO;
import org.opensingular.singular.flow.core.dto.IInstanceDTO;
import org.opensingular.singular.util.wicket.datatable.BSDataTableBuilder;
import org.opensingular.singular.util.wicket.datatable.BaseDataProvider;

public class InstanciasContent extends Content implements SingularWicketContainer<InstanciasContent, Void> {

    @Inject
    private UIAdminFacade uiAdminFacade;

    private IDefinitionDTO processDefinition;

    public InstanciasContent(String id, boolean withSideBar, String processDefinitionCode) {
        super(id, false, withSideBar, false, true);
        processDefinition = uiAdminFacade.retrieveDefinitionByKey(processDefinitionCode);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        if (flowMetadataFacade.hasAccessToProcessDefinition(processDefinition.getSigla(), getUserId(), AccessLevel.LIST)) {
            BaseDataProvider<IInstanceDTO, String> dataProvider = new BaseDataProvider<IInstanceDTO, String>() {
                @Override
                public Iterator<? extends IInstanceDTO> iterator(int first, int count,
                                                                 String sortProperty, boolean ascending) {
                    return uiAdminFacade.retrieveAllInstance(first, count, sortProperty, ascending, processDefinition.getCod()).iterator();
                }

                @Override
                public long size() {
                    return uiAdminFacade.countAllInstance(processDefinition.getCod());
                }
            };

            queue(new BSDataTableBuilder<>(dataProvider)
                    .appendPropertyColumn(getMessage("label.table.column.description"), "description", IInstanceDTO::getDescricao)
                    .appendPropertyColumn(getMessage("label.table.column.time"), "delta", IInstanceDTO::getDeltaString)
                    .appendPropertyColumn(getMessage("label.table.column.date"), "date", IInstanceDTO::getDataInicialString)
                    .appendPropertyColumn(getMessage("label.table.column.delta"), "deltas", IInstanceDTO::getDeltaAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.dates"), "dates", IInstanceDTO::getDataAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.user"), "user", IInstanceDTO::getUsuarioAlocado)
                .build("processos"));
        } else {
            queue(new WebMarkupContainer("processos").setVisible(false));
            error(getString("error.user.without.access.to.process"));
        }
        
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        RepeatingView breadCrumb = new RepeatingView(id);
        
        PageParameters pageParameters = new PageParameters().set(Content.PROCESS_DEFINITION_COD_PARAM, processDefinition.getSigla());
        
        breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(DashboardPage.class, pageParameters).toString(),
            getString("breadcrumb.statistics")));
        breadCrumb.add(createActiveBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(ProcessosPage.class, pageParameters).toString(),
            getString("breadcrumb.instances")));
        breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(MetadadosPage.class, pageParameters).toString(),
            getString("breadcrumb.metadata")));
        return breadCrumb;
    }
    
    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue(processDefinition.getNome());
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue();
    }
}
