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
    protected void carregarDefinicoes(PackageBuilder pb) {

        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");

        enabled = testForm.addFieldBoolean("enabled");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        enabled
                .as(SPackageBasic.aspect()).label("Enable");

        record.as(SPackageBasic.aspect())
                .enabled(ins -> ins.findNearestValue(enabled, Boolean.class).orElse(false))
                .dependsOn(enabled);

        recordText.as(SPackageBasic.aspect())
                .label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.as(SPackageBasic.aspect())
                .label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
