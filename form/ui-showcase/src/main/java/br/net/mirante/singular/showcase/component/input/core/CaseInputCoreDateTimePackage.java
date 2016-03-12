package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.STypeDateHour;

public class CaseInputCoreDateTimePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        final STypeDateHour inicio = tipoMyForm.addField("inicio", STypeDateHour.class);

        inicio.as(AtrBasic::new).label("Início");
        inicio.as(AtrBootstrap::new).colPreference(3);

    }

}
