/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import org.opensingular.form.wicket.component.SingularForm;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.metronic.breadcrumb.MetronicBreadcrumbBar;

public class BreadPanel extends Panel {

    private   SingularForm<?> form      = new SingularForm<>("panel-form");
    private   BSGrid          container = new BSGrid("grid");
    protected List<String>    breads    = new ArrayList<>();

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