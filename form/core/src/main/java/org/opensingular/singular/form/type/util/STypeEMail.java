/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.type.util;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.validation.ValidationErrorLevel;
import org.opensingular.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "EMail", spackage = SPackageUtil.class)
public class STypeEMail extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        asAtr().label("E-mail");
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.email());
    }
}
