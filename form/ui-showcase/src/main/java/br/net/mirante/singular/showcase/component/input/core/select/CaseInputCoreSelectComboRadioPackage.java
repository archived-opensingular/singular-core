package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectComboRadioPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //@destacar:bloco
        //View por Select
        MTipoString tipoContato1 = tipoMyForm.addCampoString("tipoContato1")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato1
                .withSelectView()
                .as(AtrBasic::new).label("Tipo Contato (Combo)");

        //@destacar:bloco
        //View por Radio
        MTipoString tipoContato2 = tipoMyForm.addCampoString("tipoContato2")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato2
                .withView(MSelecaoPorRadioView::new)
                .as(AtrBasic::new).label("Tipo Contato (Radio)");
    }
}
