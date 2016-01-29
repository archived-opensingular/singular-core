package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;


public class MultiSelectInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("$('#%s').multiSelect()", component.getMarkupId(true));
    }

}
