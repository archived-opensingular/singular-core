/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.validation;

import java.util.Optional;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseValidationBetweenFieldsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeInteger valorInicial = tipoMyForm.addFieldInteger("valorInicial");
        valorInicial.asAtrBasic().label("Valor Inicial");
        valorInicial.asAtrBasic().required();

        STypeInteger valorFinal = tipoMyForm.addFieldInteger("valorFinal");
        valorFinal.asAtrBasic().label("Valor Final");
        valorFinal.asAtrBasic().required();

        valorFinal.addInstanceValidator(validatable -> {

            SIInteger mivFinal = validatable.getInstance();
            Optional<Integer> mivInicial = mivFinal.findNearest(valorInicial).map(it -> it.getInteger());

            if (mivInicial.isPresent() && mivFinal.getInteger().compareTo(mivInicial.get()) <= 0) {
                validatable.error("O valor do campo final deve ser maior que o valor do campo inicial");
            }

        });

    }
}
