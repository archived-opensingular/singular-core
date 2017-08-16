package org.opensingular.form.flatview.mapper;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.view.SViewTab;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class TabFlatViewGenerator extends AbstractFlatViewGenerator {

    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIComposite instance = (SIComposite) context.getInstance();
        canvas.addSubtitle(context.getLabelOrName());
        SViewTab viewTab = (SViewTab) instance.getType().getView();
        for (SViewTab.STab tab : viewTab.getTabs()) {
            canvas.addSubtitle(tab.getTitle());
            for (String path : tab.getTypesName()) {
                SInstance child = instance.getField(path);
                child.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR)
                        .ifPresent(viewGenerator ->
                                callChildWrite(canvas.addChild(), child, viewGenerator));
            }
        }
    }

    void callChildWrite(DocumentCanvas newChild, SInstance child, FlatViewGenerator i) {
        i.writeOnCanvas(newChild, new FlatViewContext(child, true));
    }

}