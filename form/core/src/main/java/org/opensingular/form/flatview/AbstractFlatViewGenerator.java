package org.opensingular.form.flatview;

import org.opensingular.lib.commons.canvas.DocumentCanvas;

public abstract class AbstractFlatViewGenerator implements FlatViewGenerator {
    protected abstract void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context);

    @Override
    public void writeOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        if (context.shouldRender()) {
            doWriteOnCanvas(canvas, context);
        }
    }

}