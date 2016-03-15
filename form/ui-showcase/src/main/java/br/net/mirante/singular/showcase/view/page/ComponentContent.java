/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page;

import br.net.mirante.singular.showcase.component.ShowCaseTable;
import br.net.mirante.singular.util.wicket.tab.BSTabPanel;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.template.Content;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import static br.net.mirante.singular.showcase.component.ShowCaseTable.ShowCaseItem;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class ComponentContent extends Content implements SingularWicketContainer<ComponentContent, Void> {

    private ShowCaseItem showCaseItem;

    public ComponentContent(String id, IModel<String> componentName) {
        super(id, false, false);
        showCaseItem = new ShowCaseTable().findCaseItemByComponentName(componentName.getObject());
        add(buildItemCases());
    }

    private WebMarkupContainer buildItemCases() {

        WebMarkupContainer casesContainer = new WebMarkupContainer("casesContainer");

        if (showCaseItem.getCases().size() > 1) {

            BSTabPanel bsTabPanel = new BSTabPanel("cases");

            showCaseItem.getCases().forEach(c -> {
                String name = c.getSubCaseName();
                if (name == null) {
                    name = c.getComponentName();
                }
                bsTabPanel.addTab(name, new ItemCasePanel(BSTabPanel.getTabPanelId(), $m.ofValue(c)));
            });
            casesContainer.add(bsTabPanel);

        } else if (!showCaseItem.getCases().isEmpty()) {
            casesContainer.add(new ItemCasePanel("cases", $m.ofValue(showCaseItem.getCases().get(0))));
        }

        return casesContainer;
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return (WebMarkupContainer) new Fragment(id, "breadcrumbShowCase", this).setVisible(false);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        if (showCaseItem != null) {
            return $m.ofValue(showCaseItem.getComponentName());
        } else {
            return new ResourceModel("label.content.title");
        }
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue("");
    }
}
