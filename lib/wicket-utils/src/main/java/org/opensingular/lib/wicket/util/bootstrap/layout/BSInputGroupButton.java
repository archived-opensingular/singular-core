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

package org.opensingular.lib.wicket.util.bootstrap.layout;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;

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
