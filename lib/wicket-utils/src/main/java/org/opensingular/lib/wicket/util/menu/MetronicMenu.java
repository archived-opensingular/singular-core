/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

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
