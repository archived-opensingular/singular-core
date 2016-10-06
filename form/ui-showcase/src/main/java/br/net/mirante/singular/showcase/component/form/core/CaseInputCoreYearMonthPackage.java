/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.basic.AtrBasic;
import org.opensingular.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Componente para inserção de mês e ano.
 */
@CaseItem(componentName = "Date", subCaseName = "Mês/Ano", group = Group.INPUT)
public class CaseInputCoreYearMonthPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.addField("inicio", STypeYearMonth.class)
                .as(AtrBasic.class)
                .label("Data Início")
                .asAtrBootstrap()
                .colPreference(2);
    }

}
