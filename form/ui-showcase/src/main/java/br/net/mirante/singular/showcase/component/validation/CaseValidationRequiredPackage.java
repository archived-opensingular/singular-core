package br.net.mirante.singular.showcase.component.validation;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeInteger;

public class CaseValidationRequiredPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");
        STypeInteger mTipoInteger = tipoMyForm.addCampoInteger("qtd");
        mTipoInteger.as(AtrBasic::new).label("Quantidade");
        mTipoInteger.as(AtrCore::new).obrigatorio();

    }
}
