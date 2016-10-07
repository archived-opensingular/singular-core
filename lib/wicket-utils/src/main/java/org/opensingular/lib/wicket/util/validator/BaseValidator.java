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

package org.opensingular.lib.wicket.util.validator;

import java.util.Arrays;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public interface BaseValidator<T> extends IValidator<T> {

    default ValidationError validationError(String key) {
        ValidationError error = new ValidationError();
        error.setKeys(Arrays.asList(key));
        return error;
    }

    default ValidationError validationError(String key, String var1, Object value1) {
        return validationError(key)
            .setVariable(var1, value1);
    }

    default ValidationError validationError(String key, String var1, Object value1, String var2, Object value2) {
        return validationError(key)
            .setVariable(var1, value1)
            .setVariable(var2, value2);
    }

    default ValidationError validationError(IModel<String> errorMessage) {
        return new ValidationError()
            .setMessage(errorMessage.getObject());
    }

}