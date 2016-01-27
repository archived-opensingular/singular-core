package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;

public class SlimScrollBehaviour extends InitScriptBehaviour {

    @Override
    public String getScript(Component component, IHeaderResponse response) {
        return String.format("$('#%s').slimScroll({});", component.getMarkupId(true));
    }

}
