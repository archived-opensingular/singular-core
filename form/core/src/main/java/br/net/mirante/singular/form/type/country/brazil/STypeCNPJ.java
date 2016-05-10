/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.country.brazil;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CNPJ", spackage = SPackageCountryBrazil.class)
public class STypeCNPJ extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cnpj());
        as(AtrBasic.class).label("CNPJ").basicMask("CNPJ").tamanhoMaximo(null);
    }
}
