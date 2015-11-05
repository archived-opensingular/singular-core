package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

public interface IValidationError {
    String getMessage();
    ValidationErrorLevel getErrorLevel();
    MInstancia getInstance();
}
