package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;


public class MultiSelectInitBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component, IHeaderResponse response) {
        return String.format("$('#%s').multiSelect()", component.getMarkupId(true));
    }

}
