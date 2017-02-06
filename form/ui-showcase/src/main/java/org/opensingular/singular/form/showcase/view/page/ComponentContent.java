/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.view.page;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import javax.inject.Inject;

import org.opensingular.singular.form.showcase.component.CaseBaseForm;
import org.opensingular.singular.form.showcase.component.ShowCaseTable;
import org.opensingular.singular.form.showcase.component.CaseBaseStudio;
import org.opensingular.singular.form.showcase.component.ShowCaseType;
//import org.opensingular.singular.form.showcase.view.page.studio.StudioItemCasePanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.lib.wicket.util.tab.BSTabPanel;

public class ComponentContent extends Content implements SingularWicketContainer<ComponentContent, Void> {

    @Inject
    private ShowCaseTable showCaseTable;

    private ShowCaseTable.ShowCaseItem showCaseItem;

    public ComponentContent(String id, IModel<String> componentName) {
        super(id, false, false);
        showCaseItem = showCaseTable.findCaseItemByComponentName(componentName.getObject());
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
                if (ShowCaseType.FORM == showCaseItem.getShowCaseType()) {
                    bsTabPanel.addTab(name, new FormItemCasePanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue((CaseBaseForm) c)));
//                } else {
//                    bsTabPanel.addTab(name, new StudioItemCasePanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue((CaseBaseStudio) c)));
                }
            });
            casesContainer.add(bsTabPanel);

        } else if (!showCaseItem.getCases().isEmpty()) {
            if (ShowCaseType.STUDIO == showCaseItem.getShowCaseType()) {
//                casesContainer.add(new StudioItemCasePanel("cases", $m.ofValue((CaseBaseStudio) showCaseItem.getCases().get(0))));
            } else {
                casesContainer.add(new FormItemCasePanel("cases", $m.ofValue((CaseBaseForm) showCaseItem.getCases().get(0))));
            }
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
