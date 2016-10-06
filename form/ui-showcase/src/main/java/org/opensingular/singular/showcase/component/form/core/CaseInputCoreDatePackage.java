/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeTime;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Componente para inserção de data
 */
@CaseItem(componentName = "Date", subCaseName = "Simples", group = Group.INPUT)
public class CaseInputCoreDatePackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.addFieldDate("inicioDia")
                  .asAtr().label("Data Início");
        tipoMyForm.addField("inicioHora", STypeTime.class)
                .asAtr().label("Hora Início");
    }

}
