package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;

public class CaseCommentPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> targetForm = pb.createTipoComposto("testForm");
        targetForm.addCampoCPF("cpf")
                .as(AtrBasic.class).label("CPF");
        targetForm.addCampoCEP("cep")
                .as(AtrBasic.class).label("CEP");
        targetForm.addCampoEmail("email")
                .as(AtrBasic.class).label("E-Mail");
        targetForm.addCampoString("request")
                .as(AtrBasic.class).label("Pedido");

        targetForm.setView(MAnnotationView::new);
        super.carregarDefinicoes(pb);
    }
}
