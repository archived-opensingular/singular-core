package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
@MInfoTipo(nome = "LatitudeLongitude", pacote = SPackageCore.class)
public class STypeLatitudeLongitude extends STypeComposto<SILatitudeLongitude> {

    public static final String FIELD_LATITUDE  = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";

    public STypeLatitudeLongitude() {
        super(SILatitudeLongitude.class);
    }

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);
        addCampoString(FIELD_LATITUDE);
        addCampoString(FIELD_LONGITUDE);
    }
}
