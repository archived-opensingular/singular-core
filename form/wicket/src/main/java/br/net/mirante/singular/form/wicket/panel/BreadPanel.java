/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.metronic.breadcrumb.MetronicBreadcrumbBar;

public class BreadPanel extends Panel {

    private Form<?> form = new Form<>("panel-form");
    private BSGrid container = new BSGrid("grid");
    protected List<String> breads = new ArrayList<>();

    public BreadPanel(String id) {
        this(id, new ArrayList<>());
    }

    public BreadPanel(String id, List<String> breads) {
        super(id);
        this.breads = breads;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        rebuildForm();
    }

    private void rebuildForm() {
        add(form);
        buildTabContent();
    }

    public void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        container.newTagWithFactory("ul", true, "class='page-breadcrumb breadcrumb'", (id) -> buildBreadCrumbBar(id, breads));
        form.add(container);

    }

    public BSGrid getContainer() {
        return container;
    }

    private MetronicBreadcrumbBar buildBreadCrumbBar(String id, List<String> breadcrumbs) {
        return new MetronicBreadcrumbBar(id, breadcrumbs);
    }
}