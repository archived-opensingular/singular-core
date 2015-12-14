package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTextAreaView;

public class CaseInputCoreTextAreaPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoString("observacao")
                .withView(new MTextAreaView().setLinhas(10))
                .as(AtrBasic::new).label("Observação")
                .as(AtrBasic::new).tamanhoMaximo(5000);

    }
}
