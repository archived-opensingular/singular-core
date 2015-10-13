package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;

public class DisabledClassBehavior extends AttributeAppender {

    public static final DisabledClassBehavior INSTANCE = new DisabledClassBehavior();

    private DisabledClassBehavior() {
        super("class", "disabled", " ");
    }
    public boolean isEnabled(Component component) {
        return !component.isEnabledInHierarchy();
    }
}
