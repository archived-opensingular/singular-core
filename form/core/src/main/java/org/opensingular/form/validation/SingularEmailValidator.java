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

import org.opensingular.form.SingularFormException;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public class SingularEmailValidator extends EmailValidator {

    private static final SingularEmailValidator EMAIL_VALIDATOR = new SingularEmailValidator(false, false);

    private static final SingularEmailValidator EMAIL_VALIDATOR_WITH_TLD = new SingularEmailValidator(false, true);

    private static final SingularEmailValidator EMAIL_VALIDATOR_WITH_LOCAL = new SingularEmailValidator(true, false);

    private static final SingularEmailValidator EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD = new SingularEmailValidator(true, true);

    protected SingularEmailValidator(boolean allowLocal, boolean allowTld) {
        super(allowLocal, allowTld);
    }

    protected SingularEmailValidator(boolean allowLocal) {
        super(allowLocal);
    }

    public static SingularEmailValidator getInstance(boolean allowLocal) {
        return getInstance(allowLocal, false);
    }

    public static SingularEmailValidator getInstance(boolean allowLocal, boolean allowTld) {
        if (allowLocal) {
            if (allowTld) {
                return EMAIL_VALIDATOR_WITH_LOCAL_WITH_TLD;
            } else {
                return EMAIL_VALIDATOR_WITH_LOCAL;
            }
        } else {
            if (allowTld) {
                return EMAIL_VALIDATOR_WITH_TLD;
            } else {
                return EMAIL_VALIDATOR;
            }
        }
    }

    @Override
    protected boolean isValidUser(String user) {
        if (user == null) {
            return false;
        }
        if (user.length() > 64) {
            throw new SingularFormException("A parte destinada ao usuário do email não pode conter mais que 64 caracteres.");
        }
        final Pattern userPattern = Pattern.compile("[0-9a-zA-Z._]+");
        return userPattern.matcher(user).matches();
    }
}
