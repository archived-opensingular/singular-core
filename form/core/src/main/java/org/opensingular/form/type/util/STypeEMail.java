/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.util;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.validator.InstanceValidators;

@SInfoType(name = "EMail", spackage = SPackageUtil.class)
public class STypeEMail extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        asAtr().label("E-mail");
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.email());
    }
}
