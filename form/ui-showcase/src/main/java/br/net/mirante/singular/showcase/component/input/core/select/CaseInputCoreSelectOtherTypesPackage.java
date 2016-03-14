package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import org.joda.time.DateTime;

import java.util.Date;

public class CaseInputCoreSelectOtherTypesPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        //Select de Datas
        STypeDate tipoData = tipoMyForm.addFieldDate("inicio");
        tipoData.withSelectionOf(new Date(), DateTime.parse("2015-11-20").toDate());
        tipoData.withSelectView();

        //Select de Inteiros
        STypeInteger tipoInteiro = tipoMyForm.addFieldInteger("qtd");
        tipoInteiro.withSelectionOf(20, 40, 50);
        tipoInteiro.withSelectView();
        
        //Select with composite Dates
        STypeDate finishField = tipoMyForm.addFieldDate("finish");
        finishField
                .withSelection()
                .add(DateTime.now().toDate(),"Today")
                .add(DateTime.now().minusDays(1).toDate(),"Yesterday")
                .add(DateTime.now().minusWeeks(1).toDate(),"Last Week");
        finishField.withSelectView();

    }
}
