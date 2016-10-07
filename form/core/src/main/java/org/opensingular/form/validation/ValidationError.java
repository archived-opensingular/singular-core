/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation;

public class ValidationError implements IValidationError {

    private final Integer              instanceId;
    private final ValidationErrorLevel errorLevel;
    private final String               message;

    public ValidationError(Integer instanceId, ValidationErrorLevel level, String message) {
        this.instanceId = instanceId;
        this.message = message;
        this.errorLevel = level;
    }

    @Override
    public Integer getInstanceId() {
        return instanceId;
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
    public String toString() {
        return String.format("(%s %d '%s')",
            getErrorLevel(), getInstanceId(), getMessage());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorLevel == null) ? 0 : errorLevel.hashCode());
        result = prime * result + ((instanceId == null) ? 0 : instanceId);
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
        if (instanceId == null) {
            if (other.instanceId != null)
                return false;
        } else if (!instanceId.equals(other.instanceId))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }
}
