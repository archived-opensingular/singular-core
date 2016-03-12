package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseCustonRangeMapperPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        final STypeComposite<?> testForm = pb.createCompositeType("testForm");

        final STypeComposite<? extends SIComposite> faixaIdade = testForm.addFieldComposite("faixaIdade");
        final STypeInteger valorInicial = faixaIdade.addFieldInteger("de");
        final STypeInteger valorFinal = faixaIdade.addFieldInteger("a");

        faixaIdade.as(AtrBasic::new).label("Faixa de Idade");
        //@destacar
        faixaIdade.withCustomMapper(new RangeSliderMapper(valorInicial, valorFinal));

    }
}
