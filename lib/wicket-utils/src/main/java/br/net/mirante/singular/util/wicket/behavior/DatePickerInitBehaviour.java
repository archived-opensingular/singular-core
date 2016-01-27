package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;

public class DatePickerInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component, IHeaderResponse response) {
        String js = "";
        js += " if (jQuery().datepicker) { ";
        js += "     $('#%s').datepicker({ ";
        js += "         rtl: Metronic.isRTL(), ";
        js += "         orientation: 'right', ";
        js += "         autoclose: true, ";
        js += "         language: 'pt-BR' ";
        js += "     }); ";
        js += " } ";
        return String.format(js, component.getMarkupId(true));
    }
}