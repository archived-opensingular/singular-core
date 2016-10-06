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
 * Campo de texto simples
 */
@CaseItem(componentName = "String", subCaseName = "Simples", group = Group.INPUT)
public class CaseInputCoreStringPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("nomeCompleto")
                .asAtr().label("Nome Completo").maxLength(100);

        tipoMyForm.addFieldString("endereco")
                .asAtr().label("Endereço").maxLength(250);

    }
}
