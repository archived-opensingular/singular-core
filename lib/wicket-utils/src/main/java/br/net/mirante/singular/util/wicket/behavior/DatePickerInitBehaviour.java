/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import br.net.mirante.singular.util.wicket.bootstrap.datepicker.BSDatepickerConstants;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {

        String js = "";

        js += " $('#%s').datepicker({ ";
        js += "     rtl: App.isRTL(), ";
        js += "     orientation: 'right', ";
        js += "     autoclose: true, ";
        js += "     language: 'pt-BR' ";
        js += " }) ";

        String idDatepicker = component.getMarkupId();
        String idInput = component.getMarkupId();

        if (component instanceof MarkupContainer) {
            FormComponent fc = ((MarkupContainer) component).visitChildren(FormComponent.class, new IVisitor<FormComponent, FormComponent>() {
                @Override
                public void component(FormComponent object, IVisit<FormComponent> visit) {
                    visit.stop(object);
                }
            });
            if (fc != null) {
                idInput = fc.getMarkupId();
            }
        }

        js += ".on('changeDate', function(){";
        js += String.format("var input = $('#%s');", idInput);
        js += String.format("var format = $('#%s').attr('data-date-format').toUpperCase();", idDatepicker);
        js += "     if( format == 'DD/MM/YYYY' && /\\d{1,2}\\/\\d{1,2}\\/\\d{4}/.test(input.val()) ";
        js += "         || format == 'DD/MM' && /\\d{1,2}\\/\\d{1,2}/.test(input.val())) { ";
        js += "         input.trigger('" + BSDatepickerConstants.JS_CHANGE_EVENT + "');";
        js += "     } ";
        js += " }); ";

        return String.format(js, idDatepicker);
    }
}