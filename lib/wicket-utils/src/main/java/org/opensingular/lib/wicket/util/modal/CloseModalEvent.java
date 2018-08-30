package org.opensingular.lib.wicket.util.modal;

import java.util.function.Predicate;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public final class CloseModalEvent implements ICloseModalEvent {

    private final AjaxRequestTarget    target;
    private final Predicate<Component> predicate;

    public CloseModalEvent(AjaxRequestTarget target, Predicate<Component> predicate) {
        this.target = target;
        this.predicate = predicate;
    }

    @Override
    public boolean matchesBodyContent(Component bodyComponent) {
        return predicate.test(bodyComponent);
    }
    @Override
    public AjaxRequestTarget getTarget() {
        return target;
    }
}
