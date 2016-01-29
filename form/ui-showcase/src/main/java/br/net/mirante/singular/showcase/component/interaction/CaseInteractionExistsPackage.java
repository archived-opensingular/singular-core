package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInteractionExistsPackage extends SPackage {

    public STypeComposto<?> testForm;
    public STypeBoolean exists;
    public STypeComposto<SIComposite> record;
    public STypeString recordText;
    public STypeData recordDate;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        testForm = pb.createTipoComposto("testForm");

        exists = testForm.addCampoBoolean("exists");

        record = testForm.addCampoComposto("record");
        recordText = record.addCampoString("text");
        recordDate = record.addCampoData("date");

        exists.as(SPackageBasic.aspect()).label("Exists");

        record
            .withExists(ins -> ins.findNearestValue(exists, Boolean.class).orElse(false))
            .asAtrBasic().dependsOn(exists);

        recordText.asAtrBasic().label("Text");

        recordDate.asAtrBasic().label("Date");
    }
}
