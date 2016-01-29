package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInteractionEnabledPackage extends SPackage {

    public STypeComposto<?> testForm;
    public STypeBoolean enabled;
    public STypeComposto<SIComposite> record;
    public STypeString recordText;
    public STypeData recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        enabled = testForm.addCampoBoolean("enabled");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        enabled
            .as(SPackageBasic.aspect()).label("Enable");

        record.as(SPackageBasic.aspect())
            .enabled(ins -> ins.findNearestValue(enabled, Boolean.class).orElse(false))
            .dependsOn(enabled);

        recordText.as(SPackageBasic.aspect())
            .label("Text")
            .as(AtrCore::new);

        recordDate.as(SPackageBasic.aspect())
            .label("Date");
    }
}
