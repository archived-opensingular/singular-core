package br.net.mirante.singular.form.wicket.validator;

import org.apache.wicket.validation.ValidationError;

import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;

final class ValidationErrorAdapter implements IValidationError {
    private ValidationErrorLevel errorLevel;
    private org.apache.wicket.validation.ValidationError wicketError;
    public ValidationErrorAdapter(ValidationErrorLevel errorLevel, ValidationError wicketError) {
        this.errorLevel = errorLevel;
        this.wicketError = wicketError;
    }
    @Override
    public String getMessage() {
        return wicketError.getMessage();
    }
    @Override
    public ValidationErrorLevel getErrorLevel() {
        return errorLevel;
    }
}