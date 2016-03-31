/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseValidationCustomPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addFieldInteger("qtd");
        mTipoInteger.asAtrBasic().label("Quantidade");
        mTipoInteger.asAtrBasic().required();
        mTipoInteger.addInstanceValidator(validatable -> {
            if(validatable.getInstance().getInteger() > 1000){
                validatable.error("O Campo deve ser menor que 1000");
            }
        });

    }
}