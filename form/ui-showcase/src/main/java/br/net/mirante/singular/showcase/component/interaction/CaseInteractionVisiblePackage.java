package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInteractionVisiblePackage extends MPacote {

    public MTipoComposto<?> testForm;
    public MTipoBoolean visible;
    public MTipoComposto<MIComposto> record;
    public MTipoString recordText;
    public MTipoData recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        visible = testForm.addCampoBoolean("visible");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        visible.as(MPacoteBasic.aspect()).label("Visible");

        record.as(MPacoteBasic.aspect())
                .visivel(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false))
                .dependsOn(visible);

        recordText.as(MPacoteBasic.aspect())
                .label("Text");

        recordDate.as(MPacoteBasic.aspect())
                .label("Date");
    }
}
