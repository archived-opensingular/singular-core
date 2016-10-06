/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.layout;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.type.core.SIString;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewListByTable;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

/**
 * List by Table
 */
@CaseItem(componentName = "List by Table", subCaseName = "Simple Type", group = Group.LAYOUT)
public class CaseListByTableSimpleTypePackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?>                testForm = pb.createCompositeType("testForm");
        STypeList<STypeString, SIString> nomes    = testForm.addFieldListOf("nomes", STypeString.class);

        nomes.withView(SViewListByTable::new);
        nomes.withMiniumSizeOf(2);
        nomes.asAtrBootstrap().colPreference(6);
        nomes.asAtr().label("Nomes");

        nomes.getElementsType().asAtr().required();
    }

}
