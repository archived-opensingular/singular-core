/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.validation.validator;

import java.util.regex.Pattern;

import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.validation.IInstanceValidatable;

public enum MCEPValidator implements IInstanceValueValidator<SIString, String> {

    INSTANCE;

    @Override
    public void validate(IInstanceValidatable<SIString> validatable, String value) {
        if (!Pattern.matches("[0-9]{2}.[0-9]{3}-[0-9]{3}", value) || "00.000-000".equals(value)) {
            validatable.error("CEP inválido");
        }
    }
}
