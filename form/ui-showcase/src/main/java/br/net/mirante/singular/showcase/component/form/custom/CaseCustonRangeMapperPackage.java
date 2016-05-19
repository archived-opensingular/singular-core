/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.custom;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeInteger;

public class CaseCustonRangeMapperPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        STypeComposite<SIComposite> testForm = pb.createCompositeType("testForm");

        STypeComposite<SIComposite> faixaIdade = testForm.addFieldComposite("faixaIdade");
        STypeInteger valorInicial = faixaIdade.addFieldInteger("de");
        STypeInteger valorFinal = faixaIdade.addFieldInteger("a");

        faixaIdade.asAtr().label("Faixa de Idade");
        //@destacar
        faixaIdade.withCustomMapper(new RangeSliderMapper(valorInicial, valorFinal));

    }
}
