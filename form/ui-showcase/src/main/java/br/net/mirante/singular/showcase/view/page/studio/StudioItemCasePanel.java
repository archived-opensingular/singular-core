/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.studio;

import br.net.mirante.singular.showcase.component.CaseBaseStudio;
import br.net.mirante.singular.showcase.view.page.ItemCasePanel;
import br.net.mirante.singular.studio.core.CollectionGallery;
import br.net.mirante.singular.studio.wicket.SingularStudioCollectionPanel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.showcase.component.CaseBase;

import javax.inject.Inject;

public class StudioItemCasePanel extends ItemCasePanel<CaseBaseStudio> {

    private static final long serialVersionUID = 1L;


    @Inject
    private CollectionGallery gallery;

    public StudioItemCasePanel(String id, IModel<CaseBaseStudio> caseBase) {
        super(id, caseBase);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SingularStudioCollectionPanel("content", gallery.getCollectionCanvas(getCaseBase().getObject().getCollectionDefinition())));

    }

}
