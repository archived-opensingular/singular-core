/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.metronic.breadcrumb;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class MetronicBreadcrumbBar extends Panel {

    private List<String> breadcrumbs;

    public MetronicBreadcrumbBar(String id) {
        this(id, new ArrayList<>());
    }

    public MetronicBreadcrumbBar(String id, List<String> breadcrumbs) {
        super(id);
        this.breadcrumbs = breadcrumbs;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ListView<String> breadCrumbsListView = new ListView<String>("crumbs", breadcrumbs) {
            @Override
            protected void populateItem(ListItem item) {
                AjaxLink link = new AjaxLink("link") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {

                    }
                };
                link.add(new Label("linkLabel", item.getModel()));
                Label label = new Label("label", item.getModel());

                link.add($b.visibleIf($m.get(() -> item.getIndex() != breadcrumbs.size() - 1)));
                label.add($b.visibleIf($m.get(() -> item.getIndex() == breadcrumbs.size() - 1)));

                item.add(link);
                item.add(label);

            }
        };
        add(breadCrumbsListView);
    }

    @Deprecated
    public MetronicBreadcrumbBar addBreadCrumb(String breadcrumb) {
        breadcrumbs.add(breadcrumb);
        return this;
    }

    public List<String> getBreadcrumbs() {
        return breadcrumbs;
    }
}
