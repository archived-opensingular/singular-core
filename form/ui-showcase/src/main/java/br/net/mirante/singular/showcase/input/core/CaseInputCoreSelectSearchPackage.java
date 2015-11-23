package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectSearchPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        final MTipoString tipoContato = tipoMyForm.addCampoString("tipoContato", true)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato
                .withView(MSelecaoPorModalBuscaView::new);



    }
}
