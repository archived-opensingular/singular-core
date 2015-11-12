package br.net.mirante.singular.form.wicket.hepers;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;

public class TestPackage extends MPacote {

    public static final String PACOTE         = "mform.test.pack";
    public static final String TIPO_ATTACHMENT = PACOTE + ".Attachment";
    
    public MTipoComposto<?> attachment;
    public MTipoAttachment attachmentFileField;

    public TestPackage() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);
        attachment = pb.createTipoComposto("Attachment");
        attachment.as(AtrBasic::new).label("Anexo");
        attachmentFileField = attachment.addCampo("fileField", MTipoAttachment.class);
    }
}
