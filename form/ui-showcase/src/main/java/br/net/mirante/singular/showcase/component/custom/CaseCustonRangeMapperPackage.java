package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoInteger;

public class CaseCustonRangeMapperPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        final MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        final MTipoComposto<? extends MIComposto> faixaIdade = testForm.addCampoComposto("faixaIdade");
        final MTipoInteger valorInicial = faixaIdade.addCampoInteger("de");
        final MTipoInteger valorFinal = faixaIdade.addCampoInteger("a");

        faixaIdade.as(AtrBasic::new).label("Faixa de Idade");
        //@destacar
        faixaIdade.withCustomMapper(new RangeSliderMapper(valorInicial, valorFinal));

    }
}
