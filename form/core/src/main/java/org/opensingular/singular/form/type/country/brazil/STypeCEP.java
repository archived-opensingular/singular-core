/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.type.country.brazil;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.validation.ValidationErrorLevel;
import org.opensingular.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CEP", spackage = SPackageCountryBrazil.class)
public class STypeCEP extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cep());
        asAtr().label("CEP").basicMask("CEP").maxLength(null);
    }
}
