package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectPickListPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        STypeString contato = pb.createTipo("contato", STypeString.class)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        STypeLista<STypeString, SIString> contatos = tipoMyForm.addCampoListaOf("contatos", contato);
/*
        MTipoLista<MTipoString, MIString> contatos = tipoMyForm.addCampoListaOf("contatos","contato")
                .withMultiSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> contatos = tipoMyForm.addCampoListaOf("contatos","contato")
                .withMultiSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        // A escolha é essa -----
        MTipoLista<MTipoString, MIString> infoPub = tipoMyForm.addCampoListaOf("contatos", "contato", MTipoString.class);
        contatos.getTipoElemento().withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> infoPub = tipoMyForm.addCampoSelecaoMultiplaOf("contatos", "contato", MTipoString.class);
        contatos.getTipoElemento().withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> infoPub = tipoMyForm.addCampoListaOfSelecaoMultipla("contatos", "contato", MTipoString.class)
                .withMultiSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> contatos = tipoMyForm.addCampoListaOfSelecaoMultipla("contatos", "contato", MTipoString.class)
        contatos.getTipoElemento().withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

*/
        contatos
            .withView(MSelecaoMultiplaPorPicklistView::new)
            .as(AtrBasic::new).label("Informações Públicas");
    }

}
