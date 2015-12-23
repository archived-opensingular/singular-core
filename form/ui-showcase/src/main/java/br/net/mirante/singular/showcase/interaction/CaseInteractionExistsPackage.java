package br.net.mirante.singular.showcase.interaction;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInteractionExistsPackage extends MPacote {

    public MTipoComposto<?>          testForm;
    public MTipoBoolean              exists;
    public MTipoComposto<MIComposto> record;
    public MTipoString               recordText;
    public MTipoData                 recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        exists = testForm.addCampoBoolean("exists");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        exists.as(MPacoteBasic.aspect()).label("Exists");

        record
            .withExists(ins -> ins.findNearestValue(exists, Boolean.class).orElse(false))
            .asAtrBasic()
            .dependsOn(exists);

        recordText.asAtrBasic()
            .label("Text");

        recordDate.asAtrBasic()
            .label("Date");
    }
}
