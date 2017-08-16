package org.opensingular.form.flatview;

import org.opensingular.form.type.country.brazil.STypeUF;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

public class UFFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        canvas.addFormItem(new FormItem("UF",
                context.getInstance().getValue(STypeUF.class, i -> i.nome),
                context.getInstance().asAtrBootstrap().getColPreference()));
    }
}