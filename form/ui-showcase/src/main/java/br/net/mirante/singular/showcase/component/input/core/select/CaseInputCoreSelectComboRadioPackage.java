package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectComboRadioPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //@destacar:bloco
        //View por Select
        STypeString tipoContato1 = tipoMyForm.addCampoString("tipoContato1")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato1
                .withSelectView()
                .as(AtrBasic::new).label("Tipo Contato (Combo)");

        //@destacar:bloco
        //View por Radio
        STypeString tipoContato2 = tipoMyForm.addCampoString("tipoContato2")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato2
                .withRadioView()
                .as(AtrBasic::new).label("Tipo Contato (Radio) - Horizontal");



        STypeString tipoContato3 = tipoMyForm.addCampoString("tipoContato3")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");


        tipoContato3
                .as(AtrBasic::new)
                .label("Tipo Contato (Radio) - Vertical");

        //@destacar:bloco
        //View por Radio com layout vertical
        tipoContato3
                .withView(new MSelecaoPorRadioView().layoutVertical());
        //@destacar:fim

    }
}
