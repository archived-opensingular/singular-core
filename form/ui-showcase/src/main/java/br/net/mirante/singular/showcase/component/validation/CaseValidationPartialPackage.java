package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;

public class CaseValidationPartialPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        //@destacar
        testForm.addCampoString("obrigatorio_1")
                .as(AtrBasic::new).label("Obrigatorio 1")
                .as(AtrCore::new).obrigatorio();
        testForm.addCampoInteger("obrigatorio_2")
                .as(AtrBasic::new).label("Obrigatorio 2")
                .as(AtrCore::new).obrigatorio();
        testForm.addCampoEmail("obrigatorio_3")
                .as(AtrBasic::new).label("Obrigatorio 3")
                .as(AtrCore::new).obrigatorio();

    }
}
