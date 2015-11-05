package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.MInstancia;

public class ValidationError implements IValidationError {

    private final MInstancia           instance;
    private final ValidationErrorLevel errorLevel;
    private final String               message;

    public ValidationError(MInstancia instance, ValidationErrorLevel level, String message) {
        this.instance = instance;
        this.message = message;
        this.errorLevel = level;
    }

    @Override
    public MInstancia getInstance() {
        return instance;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ValidationErrorLevel getErrorLevel() {
        return errorLevel;
    }
}
