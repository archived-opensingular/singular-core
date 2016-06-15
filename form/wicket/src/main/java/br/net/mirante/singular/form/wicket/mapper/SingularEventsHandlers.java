package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.PackageResourceReference;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class SingularEventsHandlers extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(forReference(new PackageResourceReference(SingularEventsHandlers.class, "SingularEventsHandlers.js")));
    }

}
