package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;

public class RequiredLabelIndicatorBehavior extends AttributeAppender {

    public static final RequiredLabelIndicatorBehavior INSTANCE = new RequiredLabelIndicatorBehavior();

    private RequiredLabelIndicatorBehavior() {
        super("class", "disabled", " ");
    }
    public boolean isEnabled(Component component) {
        return !component.isEnabledInHierarchy();
    }
}
