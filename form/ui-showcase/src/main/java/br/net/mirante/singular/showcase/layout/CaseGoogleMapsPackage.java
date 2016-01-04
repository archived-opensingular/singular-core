package br.net.mirante.singular.showcase.layout;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoLatitudeLongitude;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
public class CaseGoogleMapsPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        MTipoComposto<? extends MIComposto> form = pb.createTipoComposto("testForm");
        form.addCampo("coordenada", MTipoLatitudeLongitude.class);
    }
}
