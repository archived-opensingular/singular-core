package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IInstanceValidatable<I extends MInstancia> {

    I getInstance();

    IInstanceValidatable<I> setDefaultLevel(ValidationErrorLevel level);

    ValidationErrorLevel getDefaultLevel();

    IValidationError error(IValidationError error);

    IValidationError error(String msg);

    IValidationError error(ValidationErrorLevel level, IValidationError error);

    IValidationError error(ValidationErrorLevel level, String msg);
}
