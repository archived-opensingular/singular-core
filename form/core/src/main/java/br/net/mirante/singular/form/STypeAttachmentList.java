package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.core.SPackageCore;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;

@SInfoType(name = "STypeAttachmentList", spackage = SPackageCore.class)
public class STypeAttachmentList extends STypeList<STypeAttachment, SIAttachment> {

    void setElementsTypeFieldName(String fieldName) {
        setElementsType(extendType(fieldName, STypeAttachment.class));
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        tb.getType().asAtr().displayString(context -> {
            final StringBuilder displayString = new StringBuilder();
            if (context.instance() instanceof SIList) {
                ((SIList<?>) context.instance()).getChildren()
                        .stream()
                        .map(i -> (SIAttachment) i)
                        .map(SIAttachment::toStringDisplayDefault)
                        .forEach(name -> {
                            if (!displayString.toString().isEmpty()) {
                                displayString.append(", ");
                            }
                            displayString.append(name);
                        });
            }
            return displayString.toString();
        });
    }
}
