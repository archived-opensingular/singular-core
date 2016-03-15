/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public enum MTelefoneNacionalValidator implements IInstanceValueValidator<SIString, String> {

    INSTANCE();

    public static final Pattern VALIDATE_PATTERN = Pattern.compile("\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}");

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        final Matcher matcher = VALIDATE_PATTERN.matcher(value);
        if (!matcher.find()) {
            validatable.error("Número de telefone inválido");
        }
    }
}
