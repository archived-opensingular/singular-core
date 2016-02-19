package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;


public class BSSelectInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("$('#%s').selectpicker()", component.getMarkupId(true));
    }

}
