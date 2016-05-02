/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.util.brasil;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.SPackageUtil;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CPF", spackage = SPackageUtil.class)
public class STypeCPF extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cpf());
        as(AtrBasic.class).label("CPF").basicMask("CPF").tamanhoMaximo(null);
    }
}