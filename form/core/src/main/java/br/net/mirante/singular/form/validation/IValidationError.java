package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance2;

public interface IValidationError {
    String getMessage();
    ValidationErrorLevel getErrorLevel();
    SInstance2 getInstance();
}
