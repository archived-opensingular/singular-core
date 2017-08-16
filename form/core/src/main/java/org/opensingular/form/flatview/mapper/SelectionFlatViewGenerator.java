package org.opensingular.form.flatview.mapper;

import org.opensingular.form.SInstance;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.provider.AtrProvider;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

public class SelectionFlatViewGenerator extends AbstractFlatViewGenerator {

    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SInstance instance = context.getInstance();
        AtrProvider atrProvider = instance.asAtrProvider();
        String displayValue = atrProvider.getDisplayFunction()
                .apply(atrProvider.getConverter().toObject(instance));
        canvas.addFormItem(new FormItem(context.getLabelOrName(), displayValue, instance.asAtrBootstrap().getColPreference()));
    }

}