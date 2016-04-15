/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInteractionVisiblePackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean visible;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

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
