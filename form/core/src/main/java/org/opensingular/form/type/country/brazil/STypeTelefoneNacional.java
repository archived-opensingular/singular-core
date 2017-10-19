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

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.SInfoType;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.validation.validator.InstanceValidators;
import org.opensingular.lib.commons.util.Loggable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SInfoType(name = "TelefoneNacional", spackage = SPackageCountryBrazil.class)
public class STypeTelefoneNacional extends STypeString implements Loggable {

    private static final Pattern NOT_NUMBER_PATTERN = Pattern.compile("[^\\d]");
    private static final Pattern PHONE_PATTERN      = Pattern.compile("(\\d{2})(\\d{1,4}|\\d{5}){0,1}(\\d{1,4}){0,1}");

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.telefoneNacional());
        asAtr().maxLength(15).label("Telefone");
    }

    @Override
    public String convert(Object value) {
        try {
            return format(super.convert(value));
        } catch (Exception e) {
            getLogger().trace(e.getMessage(), e);
            return String.valueOf(value);
        }
    }

    public String format(String value) {
        if (value == null) {
            return null;
        }
        return "(" + extractDDD(value) + ") " + extractNumber(value);
    }

    private Matcher getPhoneMatcher(String number) {
        String unformated;

        if (number == null) {
            return null;
        }
        unformated = unformat(number);
        unformated = removeZeroIfNeeded(unformated);
        return PHONE_PATTERN.matcher(unformated);
    }

    String extractDDD(String value) {
        final Matcher phoneMatcher = getPhoneMatcher(value);
        if (phoneMatcher == null) {
            return null;
        }
        if (phoneMatcher.matches()) {
            return phoneMatcher.group(1);
        }
        return value;
    }

    String extractNumber(String value) {
        final Matcher phoneMatcher = getPhoneMatcher(value);
        if (phoneMatcher == null) {
            return null;
        }
        if (phoneMatcher.matches()) {
            String number = "";
            String secondGroup = phoneMatcher.group(2);
            if (phoneMatcher.groupCount() >= 2 && secondGroup  != null) {
                number = secondGroup ;
            }
            String thirdGroup = phoneMatcher.group(3);
            if (phoneMatcher.groupCount() == 3 && thirdGroup != null) {
                number = number + "-" + thirdGroup;
            }
            return number;
        }
        return value;
    }

    /**
     * Verica se o dd está no padrao de 3 digitos, exemplos 061, revemondo o 0 a esquerda
     * @param unformated numero sem formatação
     * @return numero ajustado
     */
    private String removeZeroIfNeeded(String unformated) {
        if (Pattern.compile("0[1-9{2}]").matcher(unformated).lookingAt()) {
            return unformated.replaceFirst("0", "");
        }
        return unformated;
    }

    String unformat(String formated) {
        if (formated == null) {
            return null;
        }
        return NOT_NUMBER_PATTERN.matcher(formated).replaceAll("");
    }

}