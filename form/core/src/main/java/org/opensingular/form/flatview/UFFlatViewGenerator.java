package org.opensingular.form.flatview;

import org.opensingular.form.type.country.brazil.STypeUF;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class UFFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        canvas.label("UF", context.getInstance().getValue(STypeUF.class, i -> i.nome));
    }
}