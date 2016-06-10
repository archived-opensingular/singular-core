/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.map;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.util.STypeLatitudeLongitude;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Para adicionar um marcador basta clicar no posição do mapa.
 */
@CaseItem(componentName = "Google Maps", group = Group.MAPS)
public class CaseGoogleMapsPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        STypeComposite<?> form = pb.createCompositeType("testForm");
        STypeLatitudeLongitude campoCoordenada = form.addField("coordenada", STypeLatitudeLongitude.class);

        campoCoordenada.asAtr().required();
    }
}
