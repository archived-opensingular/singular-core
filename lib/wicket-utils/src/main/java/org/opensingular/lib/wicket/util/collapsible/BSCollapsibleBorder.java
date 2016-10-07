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

package org.opensingular.lib.wicket.util.collapsible;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class BSCollapsibleBorder extends Border {

    private WebMarkupContainer anchor;
    private WebMarkupContainer collapsible;
    private Component parent;
    private boolean expandByDefault;


    public BSCollapsibleBorder(String id, IModel<String> headerText, boolean expandByDefault) {
        this(id, headerText, expandByDefault, null);
    }

    public BSCollapsibleBorder(String id, IModel<String> headerText, boolean expandByDefault, Component parent) {
        super(id);
        this.parent = parent;
        this.expandByDefault = expandByDefault;
        addToBorder(buildAnchor(headerText));
        addToBorder(buildCollapisble());
        addCollapseLogic();
    }

    private Component buildCollapisble() {
        collapsible = new WebMarkupContainer("collapsible");
        return collapsible;
    }

    private Component buildAnchor(IModel<String> headerText) {
        anchor = new WebMarkupContainer("anchor");
        anchor.add(new Label("headerText", headerText));
        return anchor;
    }

    private void addCollapseLogic() {
        anchor.setOutputMarkupId(true);
        collapsible.setOutputMarkupId(true);

        anchor.add($b.attr("href", "#" + collapsible.getMarkupId()));

        if (expandByDefault) {
            collapsible.add($b.classAppender("in"));
        }

        if (parent != null) {
            parent.setOutputMarkupId(true);
            parent.add($b.attr("aria-multiselectable", "true"));
            anchor.add($b.attr("data-parent", "#" + parent.getMarkupId()));
        }

    }
}
