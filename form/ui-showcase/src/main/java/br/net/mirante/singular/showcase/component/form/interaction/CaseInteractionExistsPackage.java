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

public class CaseInteractionExistsPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean exists;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeDate recordDate;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");

        exists = testForm.addFieldBoolean("exists");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldDate("date");

        exists.as(SPackageBasic.aspect()).label("Exists");

        record
                .withExists(ins -> ins.findNearestValue(exists, Boolean.class).orElse(false))
                .asAtr().dependsOn(exists);

        recordText.asAtr().label("Text")
                .asAtrBootstrap().colPreference(3);

        recordDate.asAtr().label("Date")
                .asAtrBootstrap().colPreference(2);
    }
}
