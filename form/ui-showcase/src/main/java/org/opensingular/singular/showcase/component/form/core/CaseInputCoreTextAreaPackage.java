/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Campo Text Area
 */
@CaseItem(componentName = "String", subCaseName = "Text Area", group = Group.INPUT)
public class CaseInputCoreTextAreaPackage extends SPackage {

    @Override
    //@formatter:off
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("observacao1")
                .withTextAreaView()
                .asAtr().label("Observação (default)");

        tipoMyForm.addFieldString("observacao2")
            .withTextAreaView(view->view.setLines(2))
            .asAtr()
            .label("Observação (2 linhas e 500 de limite)")
            .maxLength(500);

        tipoMyForm.addFieldString("observacao3")
                .withTextAreaView(view->view.setLines(10))
                .asAtr()
                .label("Observação (10 linhas e 5000 de limite)")
                .maxLength(5000);
    }
}
