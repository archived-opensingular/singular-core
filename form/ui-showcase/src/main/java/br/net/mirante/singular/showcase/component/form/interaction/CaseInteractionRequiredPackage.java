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
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");

        required = testForm.addFieldBoolean("required");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        required.as(SPackageBasic.aspect()).label("Required");

        recordText.as(SPackageBasic.aspect())
                .label("Text")
                .dependsOn(required)
                .asAtr().required(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .asAtrBootstrap().colPreference(3);

        recordDate.as(SPackageBasic.aspect())
                .label("Date").dependsOn(required)
                .asAtr().required(ins -> ins.findNearestValue(required, Boolean.class).orElse(false))
                .asAtrBootstrap().colPreference(2);
    }
}
