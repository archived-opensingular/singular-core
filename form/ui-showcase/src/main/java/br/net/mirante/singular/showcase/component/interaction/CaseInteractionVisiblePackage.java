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

public class CaseInteractionVisiblePackage extends SPackage {

    public STypeComposite<?> testForm;
    public STypeBoolean visible;
    public STypeComposite<SIComposite> record;
    public STypeString recordText;
    public STypeData recordDate;

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        visible = testForm.addCampoBoolean("visible");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        visible.as(SPackageBasic.aspect()).label("Visible");

        record.as(SPackageBasic.aspect())
                .visivel(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false))
                .dependsOn(visible);

        recordText.as(SPackageBasic.aspect())
                .label("Text")
                .as(AtrBootstrap::new).colPreference(3);

        recordDate.as(SPackageBasic.aspect())
                .label("Date")
                .as(AtrBootstrap::new).colPreference(2);
    }
}
