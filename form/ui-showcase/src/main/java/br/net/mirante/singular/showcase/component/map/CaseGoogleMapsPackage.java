/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.map;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;

public class CaseGoogleMapsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        STypeComposite<?> form = pb.createCompositeType("testForm");
        STypeLatitudeLongitude campoCoordenada = form.addField("coordenada", STypeLatitudeLongitude.class);

        campoCoordenada.asAtrCore().required();
    }
}
