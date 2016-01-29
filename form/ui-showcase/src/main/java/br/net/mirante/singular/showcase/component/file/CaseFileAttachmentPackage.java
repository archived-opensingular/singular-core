package br.net.mirante.singular.showcase.component.file;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

public class CaseFileAttachmentPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        STypeAttachment anexo = tipoMyForm.addCampo("anexo", STypeAttachment.class);
        anexo.as(AtrBasic.class).label("Anexo");
    }
}
