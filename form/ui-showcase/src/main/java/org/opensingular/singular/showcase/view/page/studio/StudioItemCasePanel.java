/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.studio;

import org.opensingular.singular.showcase.component.CaseBaseStudio;
import org.opensingular.singular.showcase.view.page.ItemCasePanel;
import org.opensingular.singular.studio.core.CollectionGallery;
import org.opensingular.singular.studio.wicket.SingularStudioCollectionPanel;
import org.apache.wicket.model.IModel;

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
