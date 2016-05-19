/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.util.ProcessadorCondigoFonte;
import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;

public class ItemCasePanel extends Panel {

    private static final long serialVersionUID = 3200319871613673285L;

    private final IModel<CaseBase> caseBase;

    public ItemCasePanel(String id, IModel<CaseBase> caseBase) {
        super(id);
        this.caseBase = caseBase;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildHeaderText());

        add(buildCodeTabs());
    }

    private WebMarkupContainer buildHeaderText() {

        WebMarkupContainer headerContainer = new WebMarkupContainer("header");
        final Optional<ResourceRef> mainSource = caseBase.getObject().getMainSourceResourceName();
        final ProcessadorCondigoFonte pcf = new ProcessadorCondigoFonte(mainSource.get().getContent());
        String description = caseBase.getObject().getDescriptionHtml()
                .orElse(pcf.getJavadoc());

        headerContainer.add(new Label("description", $m.ofValue(description)).setEscapeModelStrings(false));
        headerContainer.setVisible(!description.isEmpty());

        return headerContainer;
    }

    private BSTabPanel buildCodeTabs() {

        final BSTabPanel bsTabPanel = new BSTabPanel("codes");
        final List<ResourceRef> sources = new ArrayList<>();
        final Optional<ResourceRef> mainSource = caseBase.getObject().getMainSourceResourceName();

        if (mainSource.isPresent()) {
            sources.add(mainSource.get());
        }

        sources.addAll(caseBase.getObject().getAditionalSources());

        for (ResourceRef rr : sources) {
            bsTabPanel.addTab(rr.getDisplayName(), new ItemCodePanel(
                    BSTabPanel.getTabPanelId(), $m.ofValue(rr.getContent()), $m.ofValue(rr.getExtension())));
        }

        return bsTabPanel;
    }

    protected IModel<CaseBase> getCaseBase() {
        return caseBase;
    }

    public interface ItemCaseButton extends Serializable {
        AjaxButton buildButton(String id, IModel<? extends SInstance> currentInstance);
    }
}
