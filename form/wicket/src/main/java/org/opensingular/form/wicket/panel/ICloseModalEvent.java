package org.opensingular.form.wicket.panel;

import java.io.Serializable;
import java.util.function.Predicate;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public interface ICloseModalEvent extends Serializable {

    boolean matchesBodyContent(Component bodyComponent);

    AjaxRequestTarget getTarget();

    static ICloseModalEvent of(AjaxRequestTarget target, Predicate<Component> predicate) {
        return new ICloseModalEvent() {
            @Override
            public boolean matchesBodyContent(Component bodyComponent) {
                return predicate.test(bodyComponent);
            }
            @Override
            public AjaxRequestTarget getTarget() {
                return target;
            }
        };
    }
}
