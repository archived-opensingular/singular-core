package br.net.mirante.singular.form.wicket.hepers;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;

public class TestPackage extends MPacote {

    public static final String PACOTE         = "mform.test.pack";
    public static final String TIPO_ATTACHMENT = PACOTE + ".Test";
    
    public MTipoComposto<?> baseType;
    public MTipoAttachment attachmentFileField;
    public MTipoString stringField;

    public TestPackage() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);
        baseType = pb.createTipoComposto("Test");
        baseType.as(AtrBasic::new).label("Testing Stuff");
        attachmentFileField = baseType.addCampo("fileField", MTipoAttachment.class);
        stringField = baseType.addCampoString("something");
    }
}
