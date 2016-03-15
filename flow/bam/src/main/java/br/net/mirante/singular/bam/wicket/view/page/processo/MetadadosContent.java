/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.wicket.view.page.processo;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.List;

import br.net.mirante.singular.bam.support.persistence.dto.DefinitionDTO;
import br.net.mirante.singular.bam.support.persistence.dto.MetaDataDTO;
import br.net.mirante.singular.bam.wicket.view.SingularWicketContainer;
import br.net.mirante.singular.bam.wicket.view.template.Content;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;


import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.IMetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IParameterDTO;
import br.net.mirante.singular.flow.core.dto.ITransactionDTO;
import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.bam.wicket.view.page.dashboard.DashboardPage;

public class MetadadosContent extends Content implements SingularWicketContainer<MetadadosContent, Void> {

    private DefinitionDTO processDefinition;

    public MetadadosContent(String id, boolean withSideBar, String processDefinitionCode) {
        super(id, false, withSideBar, false, true);
        processDefinition = uiAdminFacade.retrieveDefinitionByKey(processDefinitionCode);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if(flowMetadataFacade.hasAccessToProcessDefinition(processDefinition.getSigla(), getUserId(), AccessLevel.LIST)){
            final AbstractReadOnlyModel<DynamicImageResource> imageModel =
                new AbstractReadOnlyModel<DynamicImageResource>() {
                @Override
                public DynamicImageResource getObject() {
                    DynamicImageResource dir = new DynamicImageResource() {
                        @Override
                        protected byte[] getImageData(Attributes attributes) {
                            return flowMetadataFacade.processDefinitionDiagram(processDefinition);
                        }
                    };
                    dir.setFormat("image/png");
                    return dir;
                }
            };
            queue(new NonCachingImage("tabImage", imageModel));
            queue(mountMetadatas());
        } else {
            queue(new WebMarkupContainer("tabImage").setVisible(false));
            queue(new WebMarkupContainer("metadatasRow").setVisible(false));
            error(getString("error.user.without.access.to.process"));
        }
    }

    private RepeatingView mountMetadatas() {
        final List<MetaDataDTO> metadatas = uiAdminFacade.retrieveMetaData(processDefinition.getCod());
        final RepeatingView metadatasRow = new RepeatingView("metadatasRow");
        for (MetaDataDTO metadata : metadatas) {
            int max = Math.max(metadata.getTransactions().size(), 1);
            for (int i = 0; i < max; i++) {
                final WebMarkupContainer metadataRow = new WebMarkupContainer(metadatasRow.newChildId());
                metadataRow.add(createMetadatasCol(metadata, i));
                metadatasRow.add(metadataRow);
            }
        }
        return metadatasRow;
    }

    private RepeatingView createMetadatasCol(IMetaDataDTO metadata, int index) {
        final RepeatingView metadatasCol = new RepeatingView("metadatasCol");
        addRowWithSpan(metadatasCol, metadata, index, IMetaDataDTO::getTask);
        addRowWithSpan(metadatasCol, metadata, index, IMetaDataDTO::getType);
        addRowWithSpan(metadatasCol, metadata, index, IMetaDataDTO::getExecutor);
        /* Transaction */
        WebMarkupContainer metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
        if (metadata.getTransactions().size() > index) {
            metadataCol.add(new Label("metadataLabel",
                    metadata.getTransactions().get(index).getSource()
                            .concat(" → ")
                            .concat(metadata.getTransactions().get(index).getTarget())));
        } else {
            metadataCol.add(new Label("metadataLabel", ""));
        }
        metadatasCol.add(metadataCol);
        /* Parameter */
        metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
        if (metadata.getTransactions().size() > index
                && !metadata.getTransactions().get(index).getParameters().isEmpty()) {
            ITransactionDTO transaction = metadata.getTransactions().get(index);
            final RepeatingView parametersCol = new RepeatingView("metadataLabel");
            for (IParameterDTO parameter : transaction.getParameters()) {
                WebMarkupContainer parameterFragment = new Fragment(parametersCol.newChildId(), "parameterFragment", this);
                parameterFragment.add(new Label("parameterLabel", parameter.getName())
                        .add($b.attrAppender("class", (parameter.isRequired()
                                ? "label-danger" : "label-success"), " ")));
                parametersCol.add(parameterFragment);
            }
            metadataCol.add(parametersCol);
        } else {
            metadataCol.add(new Label("metadataLabel", ""));
        }
        metadatasCol.add(metadataCol);
        return metadatasCol;
    }

    private void addRowWithSpan(RepeatingView metadatasCol, IMetaDataDTO metadata, int index,
            IFunction<IMetaDataDTO, String> fValue) {
        if (index == 0) {
            WebMarkupContainer metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
            metadataCol.add(new Label("metadataLabel", fValue.apply(metadata)));
            metadatasCol.add(metadataCol);
            if (metadata.getTransactions().size() > 1) {
                metadataCol.add($b.attr("rowspan", metadata.getTransactions().size()));
            }
        }
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        RepeatingView breadCrumb = new RepeatingView(id);
        
        PageParameters pageParameters = new PageParameters().set(Content.PROCESS_DEFINITION_COD_PARAM, processDefinition.getSigla());
        
        breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(DashboardPage.class, pageParameters).toString(),
            getString("breadcrumb.statistics")));
        breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(ProcessosPage.class, pageParameters).toString(),
            getString("breadcrumb.instances")));
        breadCrumb.add(createActiveBreadCrumbLink(breadCrumb.newChildId(), 
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
