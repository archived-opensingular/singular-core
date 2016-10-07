/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.validation.validator;

import org.opensingular.form.type.core.SIString;
import org.opensingular.form.validation.IInstanceValidatable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
