/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;

public class CaseFineTunningGridPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> testForm = pb.createCompositeType("testForm");

        testForm.addFieldString("nome")
                .as(AtrBasic.class).label("Nome")
                .as(AtrBootstrap::new).colLg(7).colMd(8).colSm(9).colXs(12);
        testForm.addFieldInteger("idade")
                .as(AtrBasic.class).label("Idade")
                .as(AtrBootstrap::new).colLg(3).colMd(4).colSm(3).colXs(6);
        testForm.addFieldEmail("email")
                .as(AtrBasic.class).label("E-mail")
                .as(AtrBootstrap::new).colLg(10).colMd(12).colSm(12).colXs(12);

    }
}
