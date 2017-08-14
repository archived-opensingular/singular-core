package org.opensingular.form.flatview;

import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class SIAttachmentFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIAttachment instance = context.getInstanceAs(SIAttachment.class);
        canvas.label(instance.asAtr().getLabel(), instance.getFileName());
        canvas.breakLine();
    }
}

