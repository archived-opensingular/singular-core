/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CEP", spackage = SPackageCountryBrazil.class)
public class STypeCEP extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cep());
        asAtr().label("CEP").basicMask("CEP").maxLength(null);
    }
}
