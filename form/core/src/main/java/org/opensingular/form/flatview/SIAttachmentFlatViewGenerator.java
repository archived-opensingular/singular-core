package org.opensingular.form.flatview;

import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

public class SIAttachmentFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIAttachment instance = context.getInstanceAs(SIAttachment.class);
        canvas.addFormItem(new FormItem(instance.asAtr().getLabel(),
                instance.toStringDisplayDefault(), 12));
    }
}

