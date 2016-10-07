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

import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;
import org.opensingular.form.validation.SingularEmailValidator;

public enum MEmailValidator implements IInstanceValueValidator<SIString, String>  {

    /**
     * Local address is considered invalid
     */
    INSTANCE(false),
    /**
     * Local address is considered valid
     */
    INSTANCE_ALLOW_LOCAL_ADDRESS(true),
    ;
    
    private final boolean allowLocal;
    
    /**
     * @param allowLocal Should local addresses be considered valid?
     */
    private MEmailValidator(boolean allowLocal) {
        this.allowLocal = allowLocal;
    }

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        try {
            boolean isValid = SingularEmailValidator.getInstance(allowLocal).isValid(value);
            if(!isValid){
                validatable.error("E-mail inválido");
            }
        } catch (SingularFormException e){
            validatable.error(e.getMessage());
        }
    }
}
