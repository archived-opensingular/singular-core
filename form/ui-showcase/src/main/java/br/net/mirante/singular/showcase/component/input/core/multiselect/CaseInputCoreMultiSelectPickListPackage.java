package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectPickListPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        STypeString contato = pb.createType("contato", STypeString.class)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        STypeList<STypeString, SIString> contatos = tipoMyForm.addFieldListOf("contatos", contato);

        contatos
            .withView(SMultiSelectionByPicklistView::new)
            .as(AtrBasic::new).label("Informações Públicas");
    }

}
