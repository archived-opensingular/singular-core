/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

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
