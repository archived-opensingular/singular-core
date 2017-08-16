package org.opensingular.form.flatview.mapper;

import org.opensingular.form.SInstance;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.provider.AtrProvider;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

public class SelectionFlatViewGenerator implements FlatViewGenerator {

    @Override
    public void writeOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SInstance instance = context.getInstance();
        AtrProvider atrProvider = instance.asAtrProvider();
        String displayValue = atrProvider.getDisplayFunction()
                .apply(atrProvider.getConverter().toObject(instance));
        canvas.label(new FormItem(context.getLabelOrName(), displayValue, instance.asAtrBootstrap().getColPreference()));
    }

}
