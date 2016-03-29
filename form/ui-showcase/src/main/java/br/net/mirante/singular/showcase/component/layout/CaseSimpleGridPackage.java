/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

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
