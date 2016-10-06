/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.util.wicket.bootstrap.layout;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import org.opensingular.singular.util.wicket.model.IReadOnlyModel;
import org.opensingular.singular.util.wicket.resource.Icone;

public class BSInputGroupButton extends BSContainer<BSInputGroupButton> {

    public BSInputGroupButton(String id) {
        super(id);
        add(new AttributeAppender("class", "input-group-btn", " "));
    }

    public BSInputGroupButton appendButtonAddon(IModel<String> label, Component button) {
        return this
            .appendTag("wicket:container", new TemplatePanel("_", () -> ""
                + "<button wicket:id='" + button.getId() + "' class='btn btn-default'>"
                + Strings.escapeMarkup(StringUtils.defaultString(label.getObject()), false, false)
                + "</button>")
                .add(button));
    }

    public BSInputGroupButton appendButtonAddon(Icone icone, Component button) {
        return this
            .appendTag("wicket:container", new TemplatePanel("_", () -> ""
                + "<button wicket:id='" + button.getId() + "' class='btn btn-default'>"
                + "<i class='" + icone.getCssClass() + "'></i>"
                + "</button>")
                .add(button));
    }

    public BSInputGroupButton appendDropDownButtonAddon(IModel<String> text, BSDropDownMenu menu) {
        return this
            .appendTag("button", true, ""
                + "class='btn btn-default dropdown-toggle' "
                + "data-toggle='dropdown' "
                + "aria-haspopup='true' "
                + "aria-expanded='false' ", buttonId -> new Label(buttonId,
                (IReadOnlyModel<String>) () -> ""
                    + Strings.escapeMarkup(StringUtils.defaultString(text.getObject()), false, false)
                    + " <span class='caret'></span>")
                .setEscapeModelStrings(false))
            .appendTag("ul", true, "class='dropdown-menu'", menu);
    }
}
