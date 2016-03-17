/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.template;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class Header extends Panel {

    private boolean withTogglerButton;
    private boolean withSideBar;
    private br.net.mirante.singular.util.wicket.template.SkinOptions option;

    public Header(String id) {
        super(id);
        this.withTogglerButton = true;
        this.withSideBar = false;
    }

    public Header(String id, boolean withTogglerButton, boolean withTopAction, boolean withSideBar,
                  br.net.mirante.singular.util.wicket.template.SkinOptions option) {
        super(id);
        this.withTogglerButton = withTogglerButton;
        this.withSideBar = withSideBar;
        this.option = option;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new WebMarkupContainer("togglerButton")
                .add($b.attrAppender("class", "hide", " ", $m.ofValue(!withTogglerButton))));
        add(new WebMarkupContainer("_TopAction"));
        add(new TopMenu("_TopMenu", withSideBar, option));
        add(new WebMarkupContainer("brandLogo"));
    }
}
