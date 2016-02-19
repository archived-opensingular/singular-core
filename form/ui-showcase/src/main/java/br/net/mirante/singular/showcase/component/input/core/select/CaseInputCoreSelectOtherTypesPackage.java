package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import org.joda.time.DateTime;

import java.util.Date;

public class CaseInputCoreSelectOtherTypesPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");

        //Select de Datas
        STypeData tipoData = tipoMyForm.addCampoData("inicio");
        tipoData.withSelectionOf(new Date(), DateTime.parse("2015-11-20").toDate());
        tipoData.withSelectView();

        //Select de Inteiros
        STypeInteger tipoInteiro = tipoMyForm.addCampoInteger("qtd");
        tipoInteiro.withSelectionOf(20, 40, 50);
        tipoInteiro.withSelectView();
        
        //Select with composite Dates
        STypeData finishField = tipoMyForm.addCampoData("finish");
        finishField
                .withSelection()
                .add(DateTime.now().toDate(),"Today")
                .add(DateTime.now().minusDays(1).toDate(),"Yesterday")
                .add(DateTime.now().minusWeeks(1).toDate(),"Last Week");
        finishField.withSelectView();

    }
}
