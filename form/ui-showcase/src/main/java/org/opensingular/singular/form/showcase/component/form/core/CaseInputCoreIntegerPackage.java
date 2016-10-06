/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Campo para edição de dados inteiro
 */
@CaseItem(componentName = "Numeric", subCaseName = "Integer", group = Group.INPUT)
public class CaseInputCoreIntegerPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addFieldInteger("qtd");
        mTipoInteger.asAtr().label("Quantidade");

    }
}
