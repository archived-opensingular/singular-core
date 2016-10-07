/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Campo para inserção de dados booleanos.
 */
//@formatter:off
@CaseItem(componentName = "Boolean", group = Group.INPUT)
public class CaseInputCoreBooleanPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldBoolean("aceitaTermos")
            .asAtr().label("Aceito os termos e condições").required(true);

        tipoMyForm.addFieldBoolean("receberNotificacoes")
            //@destacar
            .withRadioView()
            .asAtr().label("Receber notificações");

        tipoMyForm.addFieldBoolean("aceitaTermos2")
            //@destacar
            .withRadioView("Aceito", "Rejeito")
            .asAtr().label("Aceito os termos e condições");
    }
}
