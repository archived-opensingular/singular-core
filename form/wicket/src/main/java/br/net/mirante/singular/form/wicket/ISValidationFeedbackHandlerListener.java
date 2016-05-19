package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.validation.IValidationError;

public interface ISValidationFeedbackHandlerListener extends Serializable {

    void onFeedbackChanged(SValidationFeedbackHandler handler,
                           Optional<AjaxRequestTarget> target,
                           Component container,
                           Collection<SInstance> baseInstances,
                           Collection<IValidationError> oldErrors, Collection<IValidationError> newErrors);
    
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