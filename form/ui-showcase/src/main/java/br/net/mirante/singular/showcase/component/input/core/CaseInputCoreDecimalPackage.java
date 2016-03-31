/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreDecimalPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldDecimal("decimalPadrao")
                .as(AtrBasic.class).label("Número decimal default");

        tipoMyForm.addFieldDecimal("decimalLongo")
                .as(AtrBasic.class).label("Decimal com 15 inteiros e 10 dígitos")
                .tamanhoInteiroMaximo(15)
                .tamanhoDecimalMaximo(10);

        super.carregarDefinicoes(pb);
    }

}