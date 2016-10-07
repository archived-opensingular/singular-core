/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component.form.interaction;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * Habilita os componentes dinamicamente.
 */
@CaseItem(componentName = "Enabled, Visible, Required", subCaseName = "Enabled", group = Group.INTERACTION)
public class CaseInteractionEnabledPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean enabled;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        super.onLoadPackage(pb);

        testForm = pb.createCompositeType("testForm");

        enabled = testForm.addFieldBoolean("enabled");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        enabled
                .asAtr().label("Enable");

        record.asAtr()
                .enabled(ins -> ins.findNearestValue(enabled, Boolean.class).orElse(false))
                .dependsOn(enabled);

        recordText.asAtr()
                .label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr()
                .label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
