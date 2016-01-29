package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.STypeDataHora;

public class CaseInputCoreDateTimePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        final STypeDataHora inicio = tipoMyForm.addCampo("inicio", STypeDataHora.class);

        inicio.as(AtrBasic::new).label("Início");
        inicio.as(AtrBootstrap::new).colPreference(3);

    }

}
