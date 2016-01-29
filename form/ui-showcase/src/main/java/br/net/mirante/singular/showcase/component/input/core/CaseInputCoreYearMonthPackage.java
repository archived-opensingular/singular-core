package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;

public class CaseInputCoreYearMonthPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampo("inicio", STypeAnoMes.class)
            .as(AtrBasic.class).label("Data inicio");
    }

}
