/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * Torna os campos obrigatórios dinamicamente.
 */
@CaseItem(componentName = "Enabled, Visible, Required", subCaseName = "Required", group = Group.INTERACTION)
public class CaseInteractionRequiredPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean required;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);

        testForm = pb.createCompositeType("testForm");

        required = testForm.addFieldBoolean("required");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        required.asAtr().label("Required");

        recordText.asAtr()
                .label("Text")
                .dependsOn(required)
                .asAtr().required(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr()
                .label("Date").dependsOn(required)
                .asAtr().required(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .asAtrBootstrap().colPreference(2);
    }
}
