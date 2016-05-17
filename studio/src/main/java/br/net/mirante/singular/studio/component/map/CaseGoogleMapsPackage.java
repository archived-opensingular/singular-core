/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.map;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeLatitudeLongitude;

public class CaseGoogleMapsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        STypeComposite<?> form = pb.createCompositeType("testForm");
        STypeLatitudeLongitude campoCoordenada = form.addField("coordenada", STypeLatitudeLongitude.class);

        campoCoordenada.asAtr().required();
    }
}
