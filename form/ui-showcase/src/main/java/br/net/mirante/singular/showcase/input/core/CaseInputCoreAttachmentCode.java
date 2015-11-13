package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.showcase.CaseCode;

public class CaseInputCoreAttachmentCode extends CaseCode {

    @Override
    public MTipo<?> createForm(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("myForm");
        tipoMyForm.addCampo("anexo", MTipoAttachment.class);
        return tipoMyForm;
    }
}
