/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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