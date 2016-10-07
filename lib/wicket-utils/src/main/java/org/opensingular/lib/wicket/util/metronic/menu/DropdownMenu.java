/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.metronic.menu;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;

import org.opensingular.lib.commons.lambda.IFunction;

public class DropdownMenu extends Panel {

    private String fixedLabel = "Novo";
    private RepeatingView menus = new RepeatingView("menus");

    public DropdownMenu(String id) {
        super(id);
    }

    public DropdownMenu(String id, String fixedLabel) {
        super(id);
        this.fixedLabel = fixedLabel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildLabel());
        add(menus);

    }

    private Component buildLabel() {
        return new Label("label", new Model<String>() {
            @Override
            public String getObject() {
                return getLabel();
            }
        });
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

    public String getLabel() {
        return fixedLabel;
    }
}
