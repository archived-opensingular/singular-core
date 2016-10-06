/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
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
                .asAtr().label("Número decimal default");

        tipoMyForm.addFieldDecimal("decimalLongo")
                .asAtr().label("Decimal com 15 inteiros e 10 dígitos")
                .integerMaxLength(15)
                .fractionalMaxLength(10);

        super.onLoadPackage(pb);
    }

}
