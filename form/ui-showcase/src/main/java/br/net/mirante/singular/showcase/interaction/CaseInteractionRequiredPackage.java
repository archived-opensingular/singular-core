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

public class CaseInteractionRequiredPackage extends MPacote {

    public MTipoComposto<?>          testForm;
    public MTipoBoolean              required;
    public MTipoComposto<MIComposto> record;
    public MTipoString               recordText;
    public MTipoData                 recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        required = testForm.addCampoBoolean("required");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        required.as(MPacoteBasic.aspect()).label("Required");

        recordText.as(MPacoteBasic.aspect())
            .label("Text")
            .dependsOn(required)
            .as(AtrCore::new).obrigatorio(ins -> ins.findNearestValue(required, Boolean.class).orElse(false));

        recordDate.as(MPacoteBasic.aspect())
            .label("Date").dependsOn(required)
            .as(AtrCore::new).obrigatorio(ins -> ins.findNearestValue(required, Boolean.class).orElse(false));
    }
}
