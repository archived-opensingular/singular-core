package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreTextAreaPackage extends MPacote {

    @Override
    //@formatter:off
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoString("observacao1")
                .withTextAreaView()
                .as(AtrBasic::new).label("Observação (default)");

        tipoMyForm.addCampoString("observacao2")
            .withTextAreaView(view->view.setLinhas(2))
            .as(AtrBasic::new).label("Observação (2 linhas e 500 de limite)")
            .as(AtrBasic::new).tamanhoMaximo(500);

        tipoMyForm.addCampoString("observacao3")
                .withTextAreaView(view->view.setLinhas(10))
                .as(AtrBasic::new).label("Observação (10 linhas e 5000 de limite)")
                .as(AtrBasic::new).tamanhoMaximo(5000);
    }
}
