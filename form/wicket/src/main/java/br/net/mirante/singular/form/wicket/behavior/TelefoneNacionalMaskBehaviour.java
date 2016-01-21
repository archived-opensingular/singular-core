package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;


public class TelefoneNacionalMaskBehaviour extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), getClass().getSimpleName() + ".js");
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("Singular.applyTelefoneNacionalMask('" + component.getMarkupId(true) + "')"));
    }

}
