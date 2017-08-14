package org.opensingular.form.flatview;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class SISimpleFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SInstance instance = context.getInstance();
        canvas.label(instance.asAtr().getLabel(), instance.toStringDisplayDefault());
    }

}