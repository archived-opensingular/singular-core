package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
@SInfoType(name = "LatitudeLongitude", spackage = SPackageCore.class)
public class STypeLatitudeLongitude extends STypeComposite<SILatitudeLongitude> {

    public static final String FIELD_LATITUDE  = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";

    public STypeLatitudeLongitude() {
        super(SILatitudeLongitude.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addFieldString(FIELD_LATITUDE);
        addFieldString(FIELD_LONGITUDE);
    }
}
