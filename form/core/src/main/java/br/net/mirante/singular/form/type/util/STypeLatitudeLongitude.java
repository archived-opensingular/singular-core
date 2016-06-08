/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.util;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
@SInfoType(name = "LatitudeLongitude", spackage = SPackageUtil.class)
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
