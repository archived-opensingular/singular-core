package br.net.mirante.singular.form.wicket.validator;

import org.apache.wicket.validation.ValidationError;

import br.net.mirante.singular.form.validation.IValidationError;

final class ValidationErrorAdapter implements IValidationError {
    private org.apache.wicket.validation.ValidationError wicketError;
    public ValidationErrorAdapter(ValidationError wicketError) {
        this.wicketError = wicketError;
    }
    @Override
    public String getMessage() {
        return wicketError.getMessage();
    }
}