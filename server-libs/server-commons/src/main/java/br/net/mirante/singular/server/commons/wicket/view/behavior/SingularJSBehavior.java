package br.net.mirante.singular.server.commons.wicket.view.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class SingularJSBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        String js =
                " Singular = Singular || {}; "
                        + " Singular.reloadContent = function () { "
                        + "     Singular.atualizarContadores(); "
                        + "     Wicket.Ajax.get({u: '%s' }); "
                        + " }; ";

        response.render(OnDomReadyHeaderItem.forScript(String.format(js, getCallbackUrl())));
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        getComponent().getPage().visitChildren((component, visit) -> {
            if (component.getId().equals("tabela")) {
                target.add(component);
                visit.stop();
            }
        });
    }

}
