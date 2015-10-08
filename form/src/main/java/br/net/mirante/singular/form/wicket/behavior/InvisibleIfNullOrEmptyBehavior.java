package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import br.net.mirante.singular.util.wicket.model.NullOrEmptyModel;

public class InvisibleIfNullOrEmptyBehavior extends Behavior {

    public static final InvisibleIfNullOrEmptyBehavior INSTANCE = new InvisibleIfNullOrEmptyBehavior();

    private InvisibleIfNullOrEmptyBehavior() {}

    @Override
    public void onConfigure(Component component) {
        component.setVisible(!NullOrEmptyModel.nullOrEmpty(component.getDefaultModelObject()));
    }
}
