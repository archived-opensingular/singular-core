package br.net.mirante.singular.form.validation;

public class ValidationError implements IValidationError {
    private String message;
    public ValidationError(String message) {
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
