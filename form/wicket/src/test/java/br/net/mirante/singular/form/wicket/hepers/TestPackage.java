package br.net.mirante.singular.form.wicket.hepers;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

public class TestPackage extends SPackage {

    public static final String PACOTE         = "mform.test.pack";
    public static final String TIPO_ATTACHMENT = PACOTE + ".Test";
    
    public STypeComposite<?> baseType;
    public STypeAttachment attachmentFileField;
    public STypeString stringField;

    public TestPackage() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);
        baseType = pb.createTipoComposto("Test");
        baseType.as(AtrBasic::new).label("Testing Stuff");
        attachmentFileField = baseType.addCampo("fileField", STypeAttachment.class);
        stringField = baseType.addCampoString("something");
    }
}
