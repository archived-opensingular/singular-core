/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.custom;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.showcase.component.Resource;

/**
 * Custom Range Mapper
 */
@CaseItem(componentName = "Custom Mapper", subCaseName = "Range Slider", group = Group.CUSTOM,
resources = {@Resource(RangeSliderMapper.class), @Resource(value = RangeSliderMapper.class, extension = "js")})
public class CaseCustomRangeMapperPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        STypeComposite<SIComposite> testForm = pb.createCompositeType("testForm");

        STypeComposite<SIComposite> faixaIdade = testForm.addFieldComposite("faixaIdade");
        STypeInteger valorInicial = faixaIdade.addFieldInteger("de");
        STypeInteger valorFinal = faixaIdade.addFieldInteger("a");

        faixaIdade.asAtr().label("Faixa de Idade");
        //@destacar
        faixaIdade.withCustomMapper(() -> new RangeSliderMapper(valorInicial, valorFinal));

    }
}
