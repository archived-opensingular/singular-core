package br.net.mirante.singular.showcase.component.map;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.MTipoLatitudeLongitude;

public class CaseGoogleMapsPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        final MTipoComposto<? extends MIComposto> form = pb.createTipoComposto("testForm");
        final MTipoLatitudeLongitude campoCoordenada = form.addCampo("coordenada", MTipoLatitudeLongitude.class);

        campoCoordenada.as(AtrCore::new).obrigatorio(true);
    }
}
