/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.interaction;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;

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

        visible.as(SPackageBasic.aspect()).label("Visible");

        record.as(SPackageBasic.aspect())
                .visible(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false))
                .dependsOn(visible);

        recordText.as(SPackageBasic.aspect())
                .label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.as(SPackageBasic.aspect())
                .label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
