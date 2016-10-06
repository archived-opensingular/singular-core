/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.layout;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;


/**
 * Configura automaticamente o tamanho das colunas do bootstrap para telas menores,
 * multiplicando pelo fator de 2, 3 e 4 para colunas md (médium), sm (small) e xs (extra small),
 * mantendo o máximo de 12.
 * Por exemplo, ao configurar o tamanho para 3, o tamanho md será 6, sm 12 e xs 12.
 */
@CaseItem(componentName = "Grid", subCaseName = "Simple", group = Group.LAYOUT)
public class CaseSimpleGridPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .asAtr().label("Nome")
                .asAtrBootstrap().colPreference(6);
        testForm.addFieldInteger("idade")
                .asAtr().label("Idade")
                .asAtrBootstrap().colPreference(2);
        testForm.addFieldEmail("email")
                .asAtr().label("E-mail")
                .asAtrBootstrap().colPreference(8);

    }
}
