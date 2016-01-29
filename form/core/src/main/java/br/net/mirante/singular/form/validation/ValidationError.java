package br.net.mirante.singular.form.validation;

import br.net.mirante.singular.form.mform.SInstance2;

public class ValidationError implements IValidationError {

    private final SInstance2 instance;
    private final ValidationErrorLevel errorLevel;
    private final String               message;

    public ValidationError(SInstance2 instance, ValidationErrorLevel level, String message) {
        this.instance = instance;
        this.message = message;
        this.errorLevel = level;
    }

    @Override
    public SInstance2 getInstance() {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorLevel == null) ? 0 : errorLevel.hashCode());
        result = prime * result + ((instance == null) ? 0 : instance.getId());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValidationError other = (ValidationError) obj;
        if (errorLevel != other.errorLevel)
            return false;
        if (instance == null) {
            if (other.instance != null)
                return false;
        } else if (!instance.equals(other.instance))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }
}
