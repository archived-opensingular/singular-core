package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorCheckView;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreMultiSelectCheckboxPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        MTipoString tipoContato = pb.createTipo("tipoContato", MTipoString.class)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        MTipoLista<MTipoString, MIString> infoPub = tipoMyForm.addCampoListaOf("infoPub", tipoContato);

        infoPub
            .withView(MSelecaoMultiplaPorCheckView::new)
            .as(AtrBasic::new).label("Informações Públicas");
    }
}
