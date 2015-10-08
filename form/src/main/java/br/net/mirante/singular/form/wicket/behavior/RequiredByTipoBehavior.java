package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public final class RequiredByTipoBehavior extends Behavior {

    public static final RequiredByTipoBehavior INSTANCE = new RequiredByTipoBehavior();

    private RequiredByTipoBehavior() {}

    public void renderHead(Component component, IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem
            .forScript("$('#" + component.getMarkupId() + "')"
                + ".append(\" <span class='required'>*</span>\");"));
    }
}