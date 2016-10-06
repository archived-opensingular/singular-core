/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.SInfoType;
import org.opensingular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CNPJ", spackage = SPackageCountryBrazil.class)
public class STypeCNPJ extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cnpj());
        asAtr().label("CNPJ").basicMask("CNPJ").maxLength(null);
    }
}
