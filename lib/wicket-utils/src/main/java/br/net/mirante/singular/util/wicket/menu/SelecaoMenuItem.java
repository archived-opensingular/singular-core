/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.menu;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;

import br.net.mirante.singular.util.wicket.behavior.FormComponentAjaxUpdateBehavior;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class SelecaoMenuItem extends AbstractMenuItem {

    private List<String> categorias;

    public SelecaoMenuItem(List<String> categorias) {
        super("menu-item");
        this.categorias = categorias;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form form = new Form<String>("form");
        final DropDownChoice<String> select = new DropDownChoice<>("select", WicketUtils.$m.ofValue(categorias.get(0)), categorias);
        form.add(select);

        select.add(new FormComponentAjaxUpdateBehavior("change", (target, component) -> {
            System.out.println("log");
        }));

        add(form);
    }

    @Override
    protected boolean configureActiveItem() {
        return false;
    }
}
