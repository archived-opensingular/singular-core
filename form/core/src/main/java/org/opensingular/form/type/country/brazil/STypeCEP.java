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

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.validator.InstanceValidators;
import org.opensingular.lib.commons.util.Loggable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SInfoType(name = "CEP", spackage = SPackageCountryBrazil.class)
public class STypeCEP extends STypeString implements Loggable {

    private static final Pattern NOT_NUMBER_PATTERN = Pattern.compile("[^\\d]");
    private static final Pattern CEP_PATTERN        = Pattern.compile("(\\d{2})(\\d{3})(\\d{3})");

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cep());
        asAtr().label("CEP").basicMask("CEP").maxLength(null);
    }

    @Override
    public String convert(Object valor) {
        try {
            return format(super.convert(valor));
        } catch (Exception e) {
            getLogger().trace(e.getMessage(), e);
            return String.valueOf(valor);
        }
    }

    private Matcher getCepMatcher(String cep) {
        String unformated;
        if (cep == null) {
            return null;
        }
        unformated = unformat(cep);
        return CEP_PATTERN.matcher(unformated);
    }

    public String format(String cep) {
        Matcher cepMatcher = getCepMatcher(cep);
        if (cepMatcher != null && cepMatcher.matches()) {
            return cepMatcher.group(1) + "." + cepMatcher.group(2) + "-" + cepMatcher.group(3);
        }
        return cep;
    }

    String unformat(String formated) {
        if (formated == null) {
            return null;
        }
        return NOT_NUMBER_PATTERN.matcher(formated).replaceAll("");
    }

}
