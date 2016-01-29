package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseCustonRangeMapperPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        final STypeComposto<?> testForm = pb.createTipoComposto("testForm");

        final STypeComposto<? extends SIComposite> faixaIdade = testForm.addCampoComposto("faixaIdade");
        final STypeInteger valorInicial = faixaIdade.addCampoInteger("de");
        final STypeInteger valorFinal = faixaIdade.addCampoInteger("a");

        faixaIdade.as(AtrBasic::new).label("Faixa de Idade");
        //@destacar
        faixaIdade.withCustomMapper(new RangeSliderMapper(valorInicial, valorFinal));

    }
}
