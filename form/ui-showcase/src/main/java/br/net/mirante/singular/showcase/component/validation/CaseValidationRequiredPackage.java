package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseValidationRequiredPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addCampoInteger("qtd");
        mTipoInteger.as(AtrBasic::new).label("Quantidade");
        mTipoInteger.as(AtrCore::new).obrigatorio();

    }
}
