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
 * Campo para inserção de dados monetários.
 */
@CaseItem(componentName = "Numeric", subCaseName = "Monetário", group = Group.INPUT)
public class CaseInputCoreMoneyPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldMonetary("monetario")
                .asAtr().label("Monetário default");

        tipoMyForm.addFieldMonetary("monetarioLongo")
                .asAtr().label("Monetário com 15 inteiros e 3 decimais")
                .integerMaxLength(15)
                .fractionalMaxLength(3);

        super.onLoadPackage(pb);
    }

}
