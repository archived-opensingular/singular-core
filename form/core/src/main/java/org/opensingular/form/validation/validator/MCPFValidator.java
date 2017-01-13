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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MCPFValidator implements IInstanceValueValidator<SIString, String> {
    INSTANCE;
    
    private static final Logger LOGGER = Logger.getLogger("MCPFValidator");

    private final List<String> invalidPatterns = Arrays.asList(
            "00000000000", "11111111111", "22222222222", "33333333333", "44444444444",
            "55555555555", "66666666666", "77777777777", "88888888888", "99999999999");

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        if (!isValid(value)) {
            validatable.error("CPF inválido");
        }
    }

    private boolean isValid(String cpf) {
        try {
            cpf = MCNPJValidator.unmask(cpf);
            if (invalidPatterns.contains(cpf)) {
                return false;
            }

            if (cpf.trim().length() != 11) {
                return false;
            }

            int i;
            int d1 = 0;
            String cpf1 = cpf.substring(0, 9);
            String cpf2 = cpf.substring(9);

            for (i = 0; i < 9; i++) {
                d1 += Integer.parseInt(cpf1.substring(i, i + 1)) * (10 - i);
            }

            d1 = 11 - (d1 % 11);
            if (d1 > 9) {
                d1 = 0;
            }

            if (Integer.parseInt(cpf2.substring(0, 1)) != d1) {
                return false;
            }

            d1 *= 2;
            for (i = 0; i < 9; i++) {
                d1 += Integer.parseInt(cpf1.substring(i, i + 1)) * (11 - i);
            }

            d1 = 11 - (d1 % 11);
            if (d1 > 9) {
                d1 = 0;
            }

            return Integer.parseInt(cpf2.substring(1, 2)) == d1;
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

        return false;
    }
}
