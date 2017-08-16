package org.opensingular.form.flatview;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

public class SISimpleFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SInstance instance = context.getInstance();
        canvas.label(new FormItem(instance.asAtr().getLabel(),
                instance.toStringDisplayDefault(), instance.asAtrBootstrap().getColPreference()));
    }

}