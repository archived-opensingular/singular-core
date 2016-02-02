package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;

public class CaseInputCoreYearMonthPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampo("inicio", STypeAnoMes.class)
                .as(AtrBasic.class)
                .label("Data Início")
                .as(AtrBootstrap::new)
                .colPreference(2);
    }

}
