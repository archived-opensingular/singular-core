package br.net.mirante.singular.form.wicket.validation;

import org.apache.wicket.validation.ValidationError;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;

final class ValidationErrorAdapter implements IValidationError {
    private final MInstancia                                   instance;
    private final ValidationErrorLevel                         errorLevel;
    private final org.apache.wicket.validation.ValidationError wicketError;
    public ValidationErrorAdapter(MInstancia instance, ValidationErrorLevel errorLevel, ValidationError wicketError) {
        this.instance = instance;
        this.errorLevel = errorLevel;
        this.wicketError = wicketError;
    }
    @Override
    public MInstancia getInstance() {
        return instance;
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