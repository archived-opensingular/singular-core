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

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

import javax.annotation.Nonnull;

/**
 * Represents a validation entry of a {@link SType}, containing the validation login and the error
 * level associated.
 *
 * @author Daniel C. Bordin on 20/08/2017.
 * @see SType#getValidators()
 * @see SType#addInstanceValidator(ValidationErrorLevel, InstanceValidator)
 */
public class ValidationEntry<I extends SInstance> {

    private final InstanceValidator<I> validator;
    private final ValidationErrorLevel errorLevel;

    public ValidationEntry(@Nonnull InstanceValidator<I> validator, @Nonnull ValidationErrorLevel errorLevel) {
        this.validator = validator;
        this.errorLevel = errorLevel;
    }

    @Nonnull
    public InstanceValidator<I> getValidator() {
        return validator;
    }

    @Nonnull
    public ValidationErrorLevel getErrorLevel() {
        return errorLevel;
    }
}
