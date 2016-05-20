/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.layout;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Permite a configuração do inicio de uma nova linha, possibilitando melhor controle do layout.
 */
@CaseItem(componentName = "Grid", subCaseName = "Row Control", group = Group.LAYOUT)
public class CaseRowControlGridPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .as(AtrBasic.class).label("Nome")
                .asAtrBootstrap().colPreference(4);
        testForm.addFieldInteger("idade")
                .as(AtrBasic.class).label("Idade")
                .asAtrBootstrap().colPreference(1);
        testForm.addFieldEmail("email")
                .as(AtrBasic.class).label("E-mail")
                .asAtrBootstrap().newRow()
                .asAtrBootstrap().colPreference(5);
    }

}
