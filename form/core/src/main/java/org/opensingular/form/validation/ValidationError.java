/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
