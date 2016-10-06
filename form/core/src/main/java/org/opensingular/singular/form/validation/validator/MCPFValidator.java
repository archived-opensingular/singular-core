/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.validation.validator;

import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.validation.IInstanceValidatable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum MCPFValidator implements IInstanceValueValidator<SIString, String> {
    INSTANCE;
    
    private static final Logger LOGGER = Logger.getLogger("MCPFValidator");

    private List<String> invalidPatterns = Arrays.asList(
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
            cpf = unmask(cpf);
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
}
