package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreDecimalPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoDecimal("decimalPadrao")
                .as(AtrBasic.class).label("Número decimal default");

        tipoMyForm.addCampoDecimal("decimalLongo")
                .as(AtrBasic.class).label("Decimal com 15 inteiros e 10 dígitos")
                .tamanhoInteiroMaximo(15)
                .tamanhoDecimalMaximo(10);

        super.carregarDefinicoes(pb);
    }

}
