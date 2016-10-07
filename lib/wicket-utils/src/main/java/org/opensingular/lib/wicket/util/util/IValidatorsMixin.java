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

package org.opensingular.lib.wicket.util.util;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.wicket.util.validator.BaseValidator;
import org.opensingular.lib.wicket.util.validator.NotFutureDateValidator;

@SuppressWarnings({ "serial" })
public interface IValidatorsMixin extends Serializable {

    default <T> IValidator<T> validator(IPredicate<T> isInvalidTest, IModel<String> errorMessage) {
        return new BaseValidator<T>() {
            @Override
            public void validate(IValidatable<T> validatable) {
                if (isInvalidTest.test(validatable.getValue())) {
                    validatable.error(validationError(errorMessage));
                }
            }
        };
    }

    default NotFutureDateValidator notFutureDate(IModel<String> errorMessage) {
        return new NotFutureDateValidator(errorMessage);
    }

    default IValidator<String> minLength(int minLength, IModel<String> errorMessage) {
        return validator(value -> value.length() < minLength, errorMessage);
    }
    default IValidator<String> maxLength(int maxLength, IModel<String> errorMessage) {
        return validator(value -> value.length() > maxLength, errorMessage);
    }
}
