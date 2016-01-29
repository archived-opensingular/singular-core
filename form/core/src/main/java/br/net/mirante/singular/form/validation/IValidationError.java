package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance;

public interface IValidationError {
    String getMessage();
    ValidationErrorLevel getErrorLevel();
    SInstance getInstance();
}
