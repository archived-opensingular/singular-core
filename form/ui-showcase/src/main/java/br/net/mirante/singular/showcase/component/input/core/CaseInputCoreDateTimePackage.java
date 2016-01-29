package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.MTipoDataHora;

public class CaseInputCoreDateTimePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        final MTipoDataHora inicio = tipoMyForm.addCampo("inicio", MTipoDataHora.class);

        inicio.as(AtrBasic::new).label("Início");
        inicio.as(AtrBootstrap::new).colPreference(3);

    }

}
