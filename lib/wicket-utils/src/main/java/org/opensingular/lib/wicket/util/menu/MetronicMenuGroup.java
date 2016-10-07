/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.menu;

import org.opensingular.lib.wicket.util.resource.Icone;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class MetronicMenuGroup extends AbstractMenuItem {

    private List<AbstractMenuItem> itens = new ArrayList<>();

    private WebMarkupContainer menuGroup = new WebMarkupContainer("menu-group");
    private WebMarkupContainer subMenu = new WebMarkupContainer("sub-menu");
    private WebMarkupContainer arrow = new WebMarkupContainer("arrow");

    public MetronicMenuGroup(String title) {
        this(null, title);
    }

    public MetronicMenuGroup(Icone icon, String title) {
        super("menu-item");
        this.icon = icon;
        this.title = title;
    }

    public void addItem(MetronicMenuItem item) {
        itens.add(item);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer iconMarkup = new WebMarkupContainer("icon");

        if (icon != null) {
            iconMarkup.add($b.classAppender(icon));
        } else {
            iconMarkup.setVisible(false);
        }

        subMenu.add(new ListView<AbstractMenuItem>("itens", itens) {
            @Override
            protected void populateItem(ListItem<AbstractMenuItem> item) {
                item.add(item.getModelObject());
            }
        });

        menuGroup.add(subMenu);
        menuGroup.add(arrow);
        menuGroup.add(iconMarkup);
        menuGroup.add(new Label("title", title));

        add(menuGroup);
    }

    @Override
    protected boolean configureActiveItem() {
        for (AbstractMenuItem i : itens) {
            if (i.configureActiveItem()) {
                subMenu.add($b.attr("style", "display: block;"));
                menuGroup.add($b.classAppender("active"));
                menuGroup.add($b.classAppender("open"));
                arrow.add($b.classAppender("open"));
                return true;
            }
        }
        return false;
    }

}
