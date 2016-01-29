package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance2;

public interface IInstanceValidatable<I extends SInstance2> {

    I getInstance();

    IInstanceValidatable<I> setDefaultLevel(ValidationErrorLevel level);

    ValidationErrorLevel getDefaultLevel();

    IValidationError error(IValidationError error);

    IValidationError error(String msg);

    IValidationError error(ValidationErrorLevel level, IValidationError error);

    IValidationError error(ValidationErrorLevel level, String msg);
}
