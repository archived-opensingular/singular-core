/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.view.template;

import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

import org.opensingular.singular.form.showcase.component.ShowCaseType;
import org.opensingular.singular.form.showcase.view.page.form.ListPage;
import org.opensingular.singular.form.showcase.view.page.studio.StudioHomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.util.wicket.metronic.menu.DropdownMenu;

public class Header extends Panel {

    private boolean withTogglerButton;
    private boolean withSideBar;
    private org.opensingular.singular.util.wicket.template.SkinOptions option;

    public Header(String id) {
        super(id);
        this.withTogglerButton = true;
        this.withSideBar = false;
    }

    public Header(String id, boolean withTogglerButton, boolean withTopAction, boolean withSideBar,
                  org.opensingular.singular.util.wicket.template.SkinOptions option) {
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
        add(buildShowcaseOptions());
        add(new TopMenu("_TopMenu", withSideBar, option));
        add(new WebMarkupContainer("brandLogo"));
    }

    private DropdownMenu buildShowcaseOptions() {
        final DropdownMenu dropdownMenu = new DropdownMenu("showcase-options", "Tipo");
        dropdownMenu.adicionarMenu(i -> new Link<String>(i) {
            @Override
            public void onClick() {
                setResponsePage(ListPage.class, ShowCaseType.buildPageParameters(ShowCaseType.FORM));
            }

            @Override
            public IModel<?> getBody() {
                return $m.ofValue("Form");
            }
        });
        dropdownMenu.adicionarMenu(i -> new Link<String>(i) {
            @Override
            public void onClick() {
                setResponsePage(StudioHomePage.class, ShowCaseType.buildPageParameters(ShowCaseType.STUDIO));
            }

            @Override
            public IModel<?> getBody() {
                return $m.ofValue("Studio");
            }
        });
        return dropdownMenu;
    }
}
