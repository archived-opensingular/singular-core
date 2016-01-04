package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreMultiSelectPickListPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        MTipoString contato = pb.createTipo("contato", MTipoString.class)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> contatos = tipoMyForm.addCampoListaOf("contatos", contato);
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
