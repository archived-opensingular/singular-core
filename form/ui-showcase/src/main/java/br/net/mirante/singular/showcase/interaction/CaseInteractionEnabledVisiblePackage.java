package br.net.mirante.singular.showcase.interaction;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInteractionEnabledVisiblePackage extends MPacote {

    public MTipoComposto<?> testForm;
    public MTipoBoolean     enabled;
    public MTipoBoolean     visible;
    public MTipoString      text;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");
        visible = testForm.addCampoBoolean("visible");
        enabled = testForm.addCampoBoolean("enabled");
        text = testForm.addCampoString("text");

        visible
            .as(MPacoteBasic.aspect()).label("Visible");

        enabled
            .as(MPacoteBasic.aspect()).label("Enabled")
            .enabled(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false));

        text
            .as(MPacoteBasic.aspect())
            .label("Text")
            .enabled(ins -> ins.findNearestValue(enabled, Boolean.class).orElse(false))
            .visivel(ins -> ins.findNearestValue(visible, Boolean.class).orElse(false))
            .dependsOn(enabled, visible);
    }
}
