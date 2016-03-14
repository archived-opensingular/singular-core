/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInteractionRequiredPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean required;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");

        required = testForm.addFieldBoolean("required");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldData("date");

        required.as(SPackageBasic.aspect()).label("Required");

        recordText.as(SPackageBasic.aspect())
                .label("Text")
                .dependsOn(required)
                .as(AtrCore::new).obrigatorio(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .as(AtrBootstrap::new).colPreference(3);

        recordDate.as(SPackageBasic.aspect())
                .label("Date").dependsOn(required)
                .as(AtrCore::new).obrigatorio(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .as(AtrBootstrap::new).colPreference(2);
    }
}
