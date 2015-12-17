package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IInstanceValidatable<I extends MInstancia> {

    I getInstance();

    void setDefaultLevel(ValidationErrorLevel level);

    void error(IValidationError error);

    IValidationError error(String msg);

    void error(ValidationErrorLevel level, IValidationError error);

    IValidationError error(ValidationErrorLevel level, String msg);
}
