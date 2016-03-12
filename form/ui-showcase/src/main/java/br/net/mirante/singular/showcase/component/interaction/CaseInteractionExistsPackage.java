package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInteractionExistsPackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean exists;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeData recordDate;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createCompositeType("testForm");

        exists = testForm.addFieldBoolean("exists");

        record = testForm.addFieldComposite("record");
        recordText = record.addFieldString("text");
        recordDate = record.addFieldData("date");

        exists.as(SPackageBasic.aspect()).label("Exists");

        record
                .withExists(ins -> ins.findNearestValue(exists, Boolean.class).orElse(false))
                .asAtrBasic().dependsOn(exists);

        recordText.asAtrBasic().label("Text")
                .as(AtrBootstrap::new).colPreference(3);

        recordDate.asAtrBasic().label("Date")
                .as(AtrBootstrap::new).colPreference(2);
    }
}
