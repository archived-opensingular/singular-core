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

package org.opensingular.lib.wicket.util.panel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;

import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;

public class FormPanel extends TemplatePanel {

    private static final String ID_FORM_BODY = "formBody";

    public FormPanel(String id, Form<?> form) {
        super(id, () -> "<form wicket:id='" + form.getId() + "'><div wicket:id='" + ID_FORM_BODY + "'></div><wicket:child/></form>");
        add(form
            .add(newFormBody(ID_FORM_BODY)));
    }

    protected Component newFormBody(String id) {
        return new WebMarkupContainer(id).setVisible(false);
    }
}
