/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.validation;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Between Fields
 */
@CaseItem(componentName = "Between Fields", group = Group.VALIDATION)
public class CaseValidationBetweenFieldsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<SIComposite> tipoMyForm = pb.createCompositeType("testForm");

        STypeInteger valorInicial = tipoMyForm.addFieldInteger("valorInicial");
        valorInicial.asAtr().label("Valor Inicial");
        valorInicial.asAtr().required();

        STypeInteger valorFinal = tipoMyForm.addFieldInteger("valorFinal");
        valorFinal.asAtr().label("Valor Final");
        valorFinal.asAtr().required();

        tipoMyForm.addInstanceValidator(validatable -> {
            SIComposite myForm = validatable.getInstance();

            int mivFinal = myForm.findNearest(valorFinal).get().getInteger();
            int mivInicial = myForm.findNearest(valorInicial).get().getInteger();

            if (mivFinal <= mivInicial) {
                validatable.error("O valor do campo final deve ser maior que o valor do campo inicial");
            }
        });

    }
}
