/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.validation;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;

public class CaseValidationPartialPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        //@destacar
        testForm.addFieldString("obrigatorio_1")
                .asAtr().label("Obrigatorio 1")
                .asAtr().required();
        testForm.addFieldInteger("obrigatorio_2")
                .asAtr().label("Obrigatorio 2")
                .asAtr().required();
        testForm.addFieldString("obrigatorio_3")
                .asAtr().label("Obrigatorio 3")
                .asAtr().required();

    }
}
