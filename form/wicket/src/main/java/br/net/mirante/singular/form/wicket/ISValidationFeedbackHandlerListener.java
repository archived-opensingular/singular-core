package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.validation.IValidationError;

public interface ISValidationFeedbackHandlerListener extends Serializable {

    void onFeedbackChanged(Optional<AjaxRequestTarget> target,
                           Component container,
                           Collection<SInstance> baseInstances,
                           Collection<IValidationError> oldErrors,
                           Collection<IValidationError> newErrors);
}