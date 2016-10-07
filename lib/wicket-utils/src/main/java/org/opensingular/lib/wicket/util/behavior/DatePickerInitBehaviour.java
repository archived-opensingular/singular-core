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
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import org.opensingular.lib.wicket.util.bootstrap.datepicker.BSDatepickerConstants;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {

        String idDatepicker = component.getMarkupId();
        String idInput = component.getMarkupId();

        if (component instanceof MarkupContainer) {
            FormComponent<?> fc = ((MarkupContainer) component)
                .visitChildren(FormComponent.class, new IVisitor<FormComponent<?>, FormComponent<?>>() {
                    @Override
                    public void component(FormComponent<?> object, IVisit<FormComponent<?>> visit) {
                        visit.stop(object);
                    }
                });
            if (fc != null) {
                idInput = fc.getMarkupId();
            }
        }

        String js = ""
            + " var $datepicker = $('#" + idDatepicker + "');"
            + " var $input = $('#" + idInput + "');"
            + " $datepicker.datepicker({ "
            + "   rtl: App.isRTL(), "
            + "   orientation: 'right', "
            + "   autoclose: true, "
            + "   language: 'pt-BR' "
            + " }) "
            + " .on('changeDate', function(){"
            + "   var input = $input; "
            + "   var format = $datepicker.data('dateFormat').toUpperCase();"
            + "   if ( format == 'DD/MM/YYYY' && /\\d{1,2}\\/\\d{1,2}\\/\\d{4}/.test(input.val()) "
            + "     || format == 'DD/MM' && /\\d{1,2}\\/\\d{1,2}/.test(input.val())) { "
            + "     input.trigger('" + BSDatepickerConstants.JS_CHANGE_EVENT + "');"
            + "   } "
            + " }); ";

        return String.format(js, idDatepicker);
    }
}