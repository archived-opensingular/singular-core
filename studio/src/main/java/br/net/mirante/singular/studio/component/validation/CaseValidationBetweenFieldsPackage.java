/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.validation;

import java.util.Optional;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.STypeInteger;

public class CaseValidationBetweenFieldsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeInteger valorInicial = tipoMyForm.addFieldInteger("valorInicial");
        valorInicial.asAtr().label("Valor Inicial");
        valorInicial.asAtr().required();

        STypeInteger valorFinal = tipoMyForm.addFieldInteger("valorFinal");
        valorFinal.asAtr().label("Valor Final");
        valorFinal.asAtr().required();

        valorFinal.addInstanceValidator(validatable -> {

            SIInteger mivFinal = validatable.getInstance();
            Optional<Integer> mivInicial = mivFinal.findNearest(valorInicial).map(it -> it.getInteger());

            if (mivInicial.isPresent() && mivFinal.getInteger().compareTo(mivInicial.get()) <= 0) {
                validatable.error("O valor do campo final deve ser maior que o valor do campo inicial");
            }

        });

    }
}
