package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;

public class CaseValidationPartialPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposite<?> testForm = pb.createTipoComposto("testForm");

        //@destacar
        testForm.addCampoString("obrigatorio_1")
                .as(AtrBasic::new).label("Obrigatorio 1")
                .as(AtrCore::new).obrigatorio();
        testForm.addCampoInteger("obrigatorio_2")
                .as(AtrBasic::new).label("Obrigatorio 2")
                .as(AtrCore::new).obrigatorio();
        testForm.addCampoString("obrigatorio_3")
                .as(AtrBasic::new).label("Obrigatorio 3")
                .as(AtrCore::new).obrigatorio();

    }
}
