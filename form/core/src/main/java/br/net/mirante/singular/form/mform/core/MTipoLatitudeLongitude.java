package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
@MInfoTipo(nome = "LatitudeLongitude", pacote = MPacoteCore.class)
public class MTipoLatitudeLongitude extends MTipoComposto<MILatitudeLongitude> {

    public static final String FIELD_LATITUDE  = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";

    public MTipoLatitudeLongitude() {
        super(MILatitudeLongitude.class);
    }

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);
        addCampoString(FIELD_LATITUDE);
        addCampoString(FIELD_LONGITUDE);
    }
}
