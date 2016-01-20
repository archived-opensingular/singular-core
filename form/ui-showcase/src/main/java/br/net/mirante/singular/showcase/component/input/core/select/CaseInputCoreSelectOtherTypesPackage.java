package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import org.joda.time.DateTime;

import java.util.Date;

public class CaseInputCoreSelectOtherTypesPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //Select de Datas
        MTipoData tipoData = tipoMyForm.addCampoData("inicio");
        tipoData.withSelectionOf(new Date(), DateTime.parse("2015-11-20").toDate());
        tipoData.withSelectView();

        //Select de Inteiros
        MTipoInteger tipoInteiro = tipoMyForm.addCampoInteger("qtd");
        tipoInteiro.withSelectionOf(20, 40, 50);
        tipoInteiro.withSelectView();
        
        //Select with composite Dates
        MTipoData finishField = tipoMyForm.addCampoData("finish");
        finishField
                .withSelection()
                .add(DateTime.now().toDate(),"Today")
                .add(DateTime.now().minusDays(1).toDate(),"Yesterday")
                .add(DateTime.now().minusWeeks(1).toDate(),"Last Week");
        finishField.withSelectView();

    }
}
