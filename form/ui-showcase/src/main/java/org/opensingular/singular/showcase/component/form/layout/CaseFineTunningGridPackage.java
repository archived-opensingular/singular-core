/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.layout;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Permite a configuração fina do tamanho das colunas, sendo possível especificar o tamanho para telas de qualquer tamanho.
 */
@CaseItem(componentName = "Grid", subCaseName = "Fine Tunning", group = Group.LAYOUT)
public class CaseFineTunningGridPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .asAtr().label("Nome")
                .asAtrBootstrap().colLg(7).colMd(8).colSm(9).colXs(12);
        testForm.addFieldInteger("idade")
                .asAtr().label("Idade")
                .asAtrBootstrap().colLg(3).colMd(4).colSm(3).colXs(6);
        testForm.addFieldEmail("email")
                .asAtr().label("E-mail")
                .asAtrBootstrap().colLg(10).colMd(12).colSm(12).colXs(12);

    }
}
