package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.attachment.SIAttachment;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

@SInfoType(name = "STypeAttachmentList", spackage = SPackageCore.class)
public class STypeAttachmentList extends STypeList<STypeAttachment, SIAttachment> {


    public void setElementsTypeFieldName(String fieldName){
        setElementsType(extendType(fieldName, STypeAttachment.class));
    }
}
