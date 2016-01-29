package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;

public class CaseInputCoreDatePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampoData("inicio")
                  .as(AtrBasic.class).label("Data inicio")
                  .as(AtrBootstrap::new).colPreference(2);
    }

}
