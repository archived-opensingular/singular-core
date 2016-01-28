package br.net.mirante.singular.util.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public abstract class InitScriptBehaviour extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(getScript(component, response)));
        super.renderHead(component, response);
    }

    public abstract String getScript(Component component, IHeaderResponse response);

}
