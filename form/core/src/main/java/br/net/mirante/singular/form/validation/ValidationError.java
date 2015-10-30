package br.net.mirante.singular.form.validation;

public class ValidationError implements IValidationError {

    private final ValidationErrorLevel errorLevel;
    private final String               message;

    public ValidationError(ValidationErrorLevel level, String message) {
        this.message = message;
        this.errorLevel = level;
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
