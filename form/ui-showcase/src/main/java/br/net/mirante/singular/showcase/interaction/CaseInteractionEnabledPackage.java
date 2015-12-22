package br.net.mirante.singular.showcase.interaction;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInteractionEnabledPackage extends MPacote {

    public MTipoComposto<?>          testForm;
    public MTipoBoolean              enabled;
    public MTipoComposto<MIComposto> record;
    public MTipoString               recordText;
    public MTipoData                 recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        enabled = testForm.addCampoBoolean("enabled");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        enabled
            .as(MPacoteBasic.aspect()).label("Enable");

        record.as(MPacoteBasic.aspect())
            .enabled(ins -> ins.findNearestValue(enabled, Boolean.class).orElse(false))
            .dependsOn(enabled);

        recordText.as(MPacoteBasic.aspect())
            .label("Text")
            .as(AtrCore::new);

        recordDate.as(MPacoteBasic.aspect())
            .label("Date");
    }
}
