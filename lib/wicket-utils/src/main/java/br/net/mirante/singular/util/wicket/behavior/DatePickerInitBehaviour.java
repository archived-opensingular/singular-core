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

        if(component instanceof MarkupContainer){
            FormComponent fc = ((MarkupContainer) component).visitChildren(FormComponent.class, new IVisitor<FormComponent, FormComponent>() {
                @Override
                public void component(FormComponent object, IVisit<FormComponent> visit) {
                    visit.stop(object);

                }
            });
            js += String.format(".on('changeDate', function(){ $('#%s').trigger('"+ BSDatepickerConstants.JS_CHANGE_EVENT+"'); });", fc.getMarkupId(true));
        }
        js += ";";

        return String.format(js, component.getMarkupId(true));
    }
}