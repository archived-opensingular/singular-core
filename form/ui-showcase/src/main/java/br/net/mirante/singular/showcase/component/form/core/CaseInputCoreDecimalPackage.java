/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Campo para inserção de dados decimais.
 */
@CaseItem(componentName = "Numeric", subCaseName = "Decimal", group = Group.INPUT)
public class CaseInputCoreDecimalPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldDecimal("decimalPadrao")
                .as(AtrBasic.class).label("Número decimal default");

        tipoMyForm.addFieldDecimal("decimalLongo")
                .as(AtrBasic.class).label("Decimal com 15 inteiros e 10 dígitos")
                .tamanhoInteiroMaximo(15)
                .tamanhoDecimalMaximo(10);

        super.onLoadPackage(pb);
    }

}
