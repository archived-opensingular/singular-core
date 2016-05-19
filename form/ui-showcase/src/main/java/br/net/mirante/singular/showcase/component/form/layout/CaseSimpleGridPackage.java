/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.layout;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;

public class CaseSimpleGridPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .as(AtrBasic.class).label("Nome")
                .asAtrBootstrap().colPreference(6);
        testForm.addFieldInteger("idade")
                .as(AtrBasic.class).label("Idade")
                .asAtrBootstrap().colPreference(2);
        testForm.addFieldEmail("email")
                .as(AtrBasic.class).label("E-mail")
                .asAtrBootstrap().colPreference(8);

    }
}
