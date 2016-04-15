/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreMoneyPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldMonetary("monetario")
                .as(AtrBasic.class).label("Monetário default");

        tipoMyForm.addFieldMonetary("monetarioLongo")
                .as(AtrBasic.class).label("Monetário com 15 inteiros e 3 decimais")
                .tamanhoInteiroMaximo(15)
                .tamanhoDecimalMaximo(3);

        super.carregarDefinicoes(pb);
    }

}
