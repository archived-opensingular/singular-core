/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.showcase.component.CaseBase;

public class StudioItemCasePanel extends ItemCasePanel {

    private static final long serialVersionUID = 1L;

    public StudioItemCasePanel(String id, IModel<CaseBase> caseBase) {
        super(id, caseBase);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

    }

}
