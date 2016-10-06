/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeDateTime;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * Componente para inserção de data e hora.
 */
@CaseItem(componentName = "Date", subCaseName = "Data e Hora", group = Group.INPUT)
public class CaseInputCoreDateTimePackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
        STypeDateTime inicio = tipoMyForm.addFieldDateTime("inicio");

        inicio.asAtr().label("Início");
        inicio.asAtrBootstrap().colPreference(3);

    }

}