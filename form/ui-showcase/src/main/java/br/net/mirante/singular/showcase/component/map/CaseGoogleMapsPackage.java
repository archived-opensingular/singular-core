package br.net.mirante.singular.showcase.component.map;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;

public class CaseGoogleMapsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        final STypeComposite<? extends SIComposite> form = pb.createTipoComposto("testForm");
        final STypeLatitudeLongitude campoCoordenada = form.addCampo("coordenada", STypeLatitudeLongitude.class);

        campoCoordenada.as(AtrCore::new).obrigatorio();
    }
}
