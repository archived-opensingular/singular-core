package org.opensingular.form.wicket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import org.opensingular.singular.commons.lambda.IConsumer;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IValidationError;

public interface ISValidationFeedbackHandlerListener extends Serializable {

    void onFeedbackChanged(SValidationFeedbackHandler handler,
                           Optional<AjaxRequestTarget> target,
                           Component container,
                           Collection<SInstance> baseInstances,
                           Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors);

    static ISValidationFeedbackHandlerListener refresh(Component... components) {
        return withTarget(t -> t.add(components));
    }
    static ISValidationFeedbackHandlerListener withTarget(IConsumer<AjaxRequestTarget> withTarget) {
        return new ISValidationFeedbackHandlerListener() {
            @Override
            public void onFeedbackChanged(SValidationFeedbackHandler handler, Optional<AjaxRequestTarget> target, Component container, Collection<SInstance> baseInstances, Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors) {
                if (target.isPresent())
                    withTarget.accept(target.get());
            }
        };
    }
}