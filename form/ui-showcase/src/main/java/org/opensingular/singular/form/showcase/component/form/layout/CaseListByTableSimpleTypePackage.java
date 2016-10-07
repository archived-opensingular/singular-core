/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.layout;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

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
