package br.net.mirante.singular.showcase.input.core.select;

import java.util.Date;

import org.joda.time.DateTime;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreSelectOtherTypesPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //Select de Datas
        MTipoData tipoData = tipoMyForm.addCampoData("inicio");
        tipoData.withSelectionOf(new Date(), DateTime.parse("2015-11-20").toDate());
        tipoData.withView(MSelecaoPorSelectView::new);

        //Select de Inteiros
        MTipoInteger tipoInteiro = tipoMyForm.addCampoInteger("qtd");
        tipoInteiro.withSelectionOf(20, 40, 50);
        tipoInteiro.withView(MSelecaoPorSelectView::new);
        
        //Select with composite Dates
        MTipoSelectItem finishField = tipoMyForm.addCampo("finish",
                                                        MTipoSelectItem.class);
        finishField.withSelectionOf(
            finishField.create(DateTime.now().toDate(),"Today"),
            finishField.create(DateTime.now().minusDays(1).toDate(),"Yesterday"),
            finishField.create(DateTime.now().minusWeeks(1).toDate(),"Last Week")
            );
        finishField.withView(MSelecaoPorSelectView::new);

    }
}
