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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisitor;

import java.util.HashMap;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    private static final long serialVersionUID = 8453390368637518965L;

    private final DatePickerSettings datePickerSettings;

    public DatePickerInitBehaviour(DatePickerSettings datePickerSettings) {
        this.datePickerSettings = datePickerSettings;
    }

    public DatePickerInitBehaviour() {
        this(null);
    }

    @Override
    public String getScript(Component component) {
        DatePickerInitScriptBuilder scriptBuilder = new DatePickerInitScriptBuilder(new HashMap<>(),
                component.getMarkupId(), getIdInput(component), datePickerSettings);
        return scriptBuilder.generateScript();
    }

    private String getIdInput(Component component) {
        String idInput = null;
        if (component instanceof MarkupContainer) {
            idInput = findFormComponentId((MarkupContainer) component);
        }
        if (idInput == null) {
            idInput = component.getMarkupId();
        }
        return idInput;
    }

    private String findFormComponentId(MarkupContainer component) {
        FormComponent<?> fc = component.visitChildren(FormComponent.class,
                (IVisitor<FormComponent<?>, FormComponent<?>>) (object, visit) -> visit.stop(object));
        if (fc != null) {
            return fc.getMarkupId();
        }
        return null;
    }

}