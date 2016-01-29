package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;

public class SlimScrollBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component) {
        return String.format("$('#%s').slimScroll({});", component.getMarkupId(true));
    }

}
