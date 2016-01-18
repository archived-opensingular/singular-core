package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class CountDownBehaviour extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        String js = "";

        js += " $('#" + component.getMarkupId(true) + "').maxlength({ ";
        js += "     alwaysShow: true,";
        js += "     validate: true";
        js += " }); ";

        response.render(OnDomReadyHeaderItem.forScript(js));
        super.renderHead(component, response);
    }

}
