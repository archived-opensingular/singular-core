/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.menu;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class MetronicMenu extends Panel {

    public List<AbstractMenuItem> itens = new ArrayList<>();

    public MetronicMenu(String id) {
        super(id);
    }

    public void addItem(AbstractMenuItem item) {
        if(itens.isEmpty()){
            item.add($b.classAppender("start"));
        }
        itens.add(item);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<AbstractMenuItem>("itens", itens) {
            @Override
            protected void populateItem(ListItem<AbstractMenuItem> item) {
                item.add(item.getModelObject());
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        itens.forEach(AbstractMenuItem::configureActiveItem);
    }

}
