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

package org.opensingular.form.validation.validator;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

import java.util.regex.Pattern;

public enum MCEPValidator implements IInstanceValueValidator<SIString, String> {

    INSTANCE;

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        if (!Pattern.matches("[0-9]{2}.[0-9]{3}-[0-9]{3}", value) || "00.000-000".equals(value)) {
            validatable.error("CEP inválido");
        }
    }
}
