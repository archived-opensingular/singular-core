package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectComboRadioPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        //@destacar:bloco
        //View por Select
        STypeString tipoContato1 = tipoMyForm.addFieldString("tipoContato1")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato1
                .withSelectView()
                .as(AtrBasic::new).label("Tipo Contato (Combo)");

        //@destacar:bloco
        //View por Radio
        STypeString tipoContato2 = tipoMyForm.addFieldString("tipoContato2")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");
        //@destacar:fim

        tipoContato2
                .withRadioView()
                .as(AtrBasic::new).label("Tipo Contato (Radio) - Horizontal");



        STypeString tipoContato3 = tipoMyForm.addFieldString("tipoContato3")
                .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");


        tipoContato3
                .as(AtrBasic::new)
                .label("Tipo Contato (Radio) - Vertical");

        //@destacar:bloco
        //View por Radio com layout vertical
        tipoContato3
                .withView(new SViewSelectionByRadio().verticalLayout());
        //@destacar:fim

    }
}
