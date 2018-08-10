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

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisitor;

public class DateBehaviour extends InitScriptBehaviour {

    private static final long serialVersionUID = 8453390368637518965L;

    private final DatePickerSettings datePickerSettings;

    public DateBehaviour(DatePickerSettings datePickerSettings) {
        this.datePickerSettings = datePickerSettings;
    }

    public DateBehaviour() {
        this(null);
    }

    @Override
    public String getScript(Component component) {
        if(datePickerSettings == null || !datePickerSettings.isHideModal().orElse(Boolean.FALSE)) {
            return new DatePickerInitScriptBuilder(new HashMap<>(),
                    component.getMarkupId(), getIdInput(component), datePickerSettings).generateScript();
        }
        return new DateInputBehavior(getIdInput(component)).generateScript();
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