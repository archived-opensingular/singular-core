/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.interaction;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SPackage;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.type.core.STypeBoolean;
import org.opensingular.singular.form.type.core.STypeDate;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;

/**
 * Exibe campos visíveis dinamicamente.
 */
@CaseItem(componentName = "Enabled, Visible, Required", subCaseName = "Visible", group = Group.INTERACTION)
public class CaseInteractionVisiblePackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean visible;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        testForm = pb.createCompositeType("testForm");

        visible = testForm.addFieldBoolean("visible");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        visible.asAtr().label("Visible");

        record.asAtr()
                .visible(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false))
                .dependsOn(visible);

        recordText.asAtr()
                .label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr()
                .label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
