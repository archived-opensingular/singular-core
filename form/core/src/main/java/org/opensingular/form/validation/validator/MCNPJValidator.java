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

public enum MCNPJValidator implements IInstanceValueValidator<SIString, String> {
    INSTANCE;
    
    private static final Logger LOGGER = Logger.getLogger("MCNPJValidator");
    private List<String> invalidPatterns = Arrays.asList(
            "00000000000000", "11111111111111", "22222222222222", "33333333333333", "44444444444444",
            "55555555555555", "66666666666666", "77777777777777", "88888888888888", "99999999999999");

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        if (!isValid(value)) {
            validatable.error("CNPJ inválido");
        }
    }

    private boolean isValid(String cnpj) {
        try {
            cnpj = unmask(cnpj);

            if (invalidPatterns.contains(cnpj)) {
                return false;
            }

            if (cnpj.trim().length() != 14) {
                return false;
            }

            char cnpjArray[] = cnpj.toCharArray();

            Integer digit1 = this.retrieveDV(cnpjArray);
            Integer digit2 = this.retrieveDV(cnpjArray, digit1);

            String dvExpected = digit1.toString() + digit2.toString();
            String dv = cnpjArray[cnpjArray.length - 2] + "" + cnpjArray[cnpjArray.length - 1];

            return dv.equals(dvExpected);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }

        return false;
    }

    private String unmask(String cnpj) {
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile("[0-9]?");
        Matcher m = p.matcher(cnpj);
        while (m.find()) {
            if (m.groupCount() > 0) {
                for (int i = 0; i < m.groupCount(); i++) {
                    sb.append(m.group(i));
                }
            }
            sb.append(m.group());
        }
        return sb.toString();
    }


    private int retrieveDV(char[] cnpjArray) {
        int factor = 5;
        int sum = 0;
        for (int i = 0; i < cnpjArray.length - 2; i++) {
            char c = cnpjArray[i];
            sum += Integer.parseInt(String.valueOf(c)) * factor;
            if (i == 3) {
                factor = 9;
            } else {
                factor--;
            }
        }

        int value = (sum) % 11;
        int dv = 11 - value;
        if ((sum) % 11 < 2) {
            dv = 0;
        }

        return dv;
    }

    private int retrieveDV(char[] cnpjArray, int prevDV) {
        int factor = 6;
        int sum = 0;
        for (int i = 0; i < cnpjArray.length - 2; i++) {
            char c = cnpjArray[i];
            sum += Integer.parseInt(String.valueOf(c)) * factor;
            if (i == 4) {
                factor = 9;
            } else {
                factor--;
            }
        }

        sum += prevDV * 2;
        int value = (sum) % 11;
        int dv = 11 - value;
        if ((sum) % 11 < 2) {
            dv = 0;
        }

        return dv;
    }
}
