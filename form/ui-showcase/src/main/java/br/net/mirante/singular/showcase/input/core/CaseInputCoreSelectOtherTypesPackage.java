package br.net.mirante.singular.showcase.input.core;

import java.util.Date;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectBSView;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;

public class CaseInputCoreSelectOtherTypesPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //Select de Datas
        MTipoData tipoData = tipoMyForm.addCampoData("inicio");
        tipoData.withSelectionOf(new Date(), new Date(2015,11,20));
        tipoData.withView(MSelecaoPorSelectBSView::new);

        //Select de Inteiros
        MTipoInteger tipoInteiro = tipoMyForm.addCampoInteger("qtd");
        tipoInteiro.withSelectionOf(20, 40, 50);
        tipoInteiro.withView(MSelecaoPorSelectBSView::new);

    }
}
