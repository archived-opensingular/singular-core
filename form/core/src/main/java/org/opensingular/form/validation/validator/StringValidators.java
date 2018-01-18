/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.validation.validator;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.InstanceValidator;

import java.util.function.Predicate;

/**
 * Classe com validadores comuns de string.
 */
public class StringValidators {

    private static InstanceValidator<SIString> predicateToValidator(Predicate<String> predicate, String errorMessage) {
        return iv -> {
            if (!predicate.test(iv.getInstanceValue(String.class))) {
                iv.error(errorMessage);
            }
        };
    }

    /**
     * Valida se a String é correspondente a expressão regular
     *
     * @param regex        a expressão regular
     * @param errorMessage a mensagem de erro
     * @return o validador
     */
    public static InstanceValidator<SIString> matches(String regex, String errorMessage) {
        return predicateToValidator(val -> val.matches(regex), errorMessage);
    }

    /**
     * Valida se a String é termina com o sufixo informado.
     *
     * @param suffix       o sufixo
     * @param errorMessage a mensagem de erro
     * @return o validador
     */
    public static InstanceValidator<SIString> endsWith(String suffix, String errorMessage) {
        return predicateToValidator(val -> val.endsWith(suffix), errorMessage);
    }

    /**
     * Valida se a String é mensagem enviada não é vazia
     *
     * @param errorMessage a mensagem de erro
     * @return o validador
     */
    public static InstanceValidator<SIString> isNotBlank(String errorMessage) {
        return predicateToValidator(StringUtils::isNotBlank, errorMessage);
    }

}