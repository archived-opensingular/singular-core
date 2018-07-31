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

package org.opensingular.lib.wicket.util.tab;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class BSTabPanel extends Panel {

    public static final String TAB_PANEL_ID = "tab-panel";
    private Map<Pair<String, Integer>, Panel> tabMap = new LinkedHashMap<>();
    private boolean withChangeUrl = false;

    public BSTabPanel(String id) {
        super(id);
    }

    public void addTab(String headerText, Panel panel) {
        if (panel.getId().equals(TAB_PANEL_ID)) {
            tabMap.put(Pair.of(headerText, panel.hashCode()), panel);
        } else {
            throw new WicketRuntimeException("O ID do panel deve ser " + TAB_PANEL_ID);
        }
    }

    public void addTab(String headerText, String idTab, Panel panel) {
        if (StringUtils.isNotBlank(idTab)) {
            panel.setMarkupId(idTab.replaceAll("[^\\da-zA-Z]", ""));
        }
        withChangeUrl = true;
        this.addTab(headerText, panel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildTabContent());
        add(buildTabControll());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (withChangeUrl) {
            //Will include the JS just in case of change Url on Tab click.
            response.render(forReference(new PackageResourceReference(BSTabPanel.class, "BSTabPanel.js")));
        }
    }

    private Component buildTabControll() {
        ListView<Pair<String, Integer>> listView = new ListView<Pair<String, Integer>>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<Pair<String, Integer>> item) {

                Panel currentPanel = tabMap.get(item.getModelObject());

                if (item.getIndex() == 0) {
                    item.add($b.classAppender("active"));
                }

                WebMarkupContainer tabAnchor = new WebMarkupContainer("tabAnchor");
                tabAnchor.add($b.attr("href", "#" + currentPanel.getMarkupId()));
                tabAnchor.add($b.attr("aria-controls", currentPanel.getMarkupId()));

                tabAnchor.add(new Label("header-text", item.getModelObject().getLeft()));

                configureChangeUrlOnTabClick(currentPanel.getMarkupId(), tabAnchor);

                item.add(tabAnchor);
            }
        };


        return listView;
    }

    /**
     * When Tab is clicked the url of page will change to have the id of the tab.
     * <p>
     * Note: This behavior is used by showcase to have static url's for each component.
     *
     * @param currentPanelId The currentPanel id.
     * @param tabAnchor      The Tab will be clicked.
     */
    private void configureChangeUrlOnTabClick(String currentPanelId, WebMarkupContainer tabAnchor) {
        if (withChangeUrl) {
            tabAnchor.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    target.appendJavaScript("window.BSTAB.changeUrl('" + currentPanelId + "','#" + currentPanelId + "')");
                }
            });
        }
    }

    private Component buildTabContent() {
        return new ListView<Pair<String, Integer>>("tab-content", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<Pair<String, Integer>> item) {

                Panel panel = tabMap.get(item.getModelObject());

                if (item.getIndex() == 0) {
                    panel.add($b.classAppender("active"));
                }

                item.add(panel);
                panel.setOutputMarkupId(true);
            }
        };
    }

}
