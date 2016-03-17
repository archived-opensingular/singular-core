/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.tab;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class BSTabPanel extends Panel {

    private static final String TAB_PANEL_ID = "tab-panel";
    private Map<Pair<String, Integer>, Panel> tabMap = new LinkedHashMap<>();

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

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildTabContent());
        add(buildTabControll());
    }

    private Component buildTabControll() {
        return new ListView<Pair<String, Integer>>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<Pair<String, Integer>> item) {

                Panel currentPanel = tabMap.get(item.getModelObject());

                if(item.getIndex() == 0){
                    item.add($b.classAppender("active"));
                }

                WebMarkupContainer tabAnchor = new WebMarkupContainer("tabAnchor");
                tabAnchor.add($b.attr("href", "#"+currentPanel.getMarkupId()));
                tabAnchor.add($b.attr("aria-controls", currentPanel.getMarkupId()));

                tabAnchor.add(new Label("header-text", item.getModelObject().getLeft()));
                item.add(tabAnchor);
            }
        };
    }

    private Component buildTabContent() {
        return new ListView<Pair<String, Integer>>("tab-content", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<Pair<String, Integer>> item) {

                Panel panel = tabMap.get(item.getModelObject());

                if(item.getIndex() == 0){
                    panel.add($b.classAppender("active"));
                }

                item.add(panel);
                panel.setOutputMarkupId(true);
            }
        };
    }

    public static String getTabPanelId() {
        return TAB_PANEL_ID;
    }
}
