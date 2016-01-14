package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;

public class CaseAnnotationPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> targetForm = pb.createTipoComposto("testForm");
        targetForm.as(AtrBasic::new).label("Pedido");

        MTipoCPF cpf = targetForm.addCampoCPF("cpf");
        cpf.as(AtrBasic.class).label("CPF");
        targetForm.addCampoCEP("cep")
                .as(AtrBasic.class).label("CEP");
        targetForm.addCampoEmail("email")
                .as(AtrBasic.class).label("E-Mail");
        MTipoString request = targetForm.addCampoString("request");
        request.as(AtrBasic.class).label("Pedido");

        //@destacar
        targetForm.setView(MAnnotationView::new);
        super.carregarDefinicoes(pb);
    }
}
