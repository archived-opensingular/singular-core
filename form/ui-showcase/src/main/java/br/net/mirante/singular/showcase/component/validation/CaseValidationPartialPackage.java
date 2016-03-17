/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;

public class CaseValidationPartialPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        //@destacar
        testForm.addFieldString("obrigatorio_1")
                .as(AtrBasic::new).label("Obrigatorio 1")
                .as(AtrCore::new).obrigatorio();
        testForm.addFieldInteger("obrigatorio_2")
                .as(AtrBasic::new).label("Obrigatorio 2")
                .as(AtrCore::new).obrigatorio();
        testForm.addFieldString("obrigatorio_3")
                .as(AtrBasic::new).label("Obrigatorio 3")
                .as(AtrCore::new).obrigatorio();

    }
}
