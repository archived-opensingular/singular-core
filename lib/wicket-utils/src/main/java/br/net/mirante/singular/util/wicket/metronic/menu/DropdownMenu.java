/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.metronic.menu;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import br.net.mirante.singular.commons.lambda.IFunction;

public class DropdownMenu extends Panel {

    private RepeatingView menus = new RepeatingView("menus");

    public DropdownMenu(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(menus);

    }

    public <T> void adicionarMenu(IFunction<String, Link<T>> funcaoConstrutora) {
        Component item = new WebMarkupContainer(menus.newChildId())
                .add(funcaoConstrutora.apply("link"));
        menus.add(item);
    }

    @Override
    public boolean isVisible() {
        return menus.size() > 0;
    }
}
