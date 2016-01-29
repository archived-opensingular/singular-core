package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        String js = "";
        js += " $('#%s').datepicker({ ";
        js += "     rtl: App.isRTL(), ";
        js += "     orientation: 'right', ";
        js += "     autoclose: true, ";
        js += "     language: 'pt-BR' ";
        js += " }); ";
        return String.format(js, component.getMarkupId(true));
    }
}