/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Componente para inserção de mês e ano.
 */
@CaseItem(componentName = "Date", subCaseName = "Mês/Ano", group = Group.INPUT)
public class CaseInputCoreYearMonthPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        tipoMyForm.addField("inicio", STypeYearMonth.class)
                .as(AtrBasic.class)
                .label("Data Início")
                .asAtrBootstrap()
                .colPreference(2);
    }

}
