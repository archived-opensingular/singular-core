/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.interaction;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Interação usando exists
 */
@CaseItem(componentName = "Enabled, Visible, Required", subCaseName = "Exists", group = Group.INTERACTION)
public class CaseInteractionExistsPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean exists;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        testForm = pb.createCompositeType("testForm");

        exists = testForm.addFieldBoolean("exists");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        exists.asAtr().label("Exists");

        record
                .withExists(ins -> ins.findNearestValue(exists, Boolean.class).orElse(false))
                .asAtr().dependsOn(exists);

        recordText.asAtr().label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr().label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
