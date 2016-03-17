/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class MetronicTheme extends Behavior {

    private static final ResourceReference CSS = new CssResourceReference(MetronicTheme.class, "metronic/theme.css");

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        tag.append("class", "tree-theme-metronic", " ");
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(CSS));
    }
}
