package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class CaseFineTunningGridPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        final MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        testForm.addCampoString("nome")
                .as(AtrBasic.class).label("Nome")
                .as(AtrBootstrap::new).colLg(7).colMd(8).colSm(9).colXs(12);
        testForm.addCampoInteger("idade")
                .as(AtrBasic.class).label("Idade")
                .as(AtrBootstrap::new).colLg(3).colMd(4).colSm(3).colXs(6);
        testForm.addCampoEmail("email")
                .as(AtrBasic.class).label("E-mail")
                .as(AtrBootstrap::new).colLg(10).colMd(12).colSm(12).colXs(12);

    }
}
