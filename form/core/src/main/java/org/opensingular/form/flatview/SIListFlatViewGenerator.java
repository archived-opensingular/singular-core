package org.opensingular.form.flatview;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

import java.util.Optional;

public class SIListFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIList<?> instance = context.getInstanceAs(SIList.class);
        DocumentCanvas subcanvas = canvas;
        if (!context.isWithoutTitle()) {
            subcanvas = canvas.addChild();
            canvas.addSubtitle(context.getLabelOrName());
        }
        for (SInstance child : instance) {
            Optional<FlatViewGenerator> aspect = child.getAspect(ASPECT_FLAT_VIEW_GENERATOR);
            if (aspect.isPresent()) {
                aspect.get().writeOnCanvas(subcanvas, new FlatViewContext(child));
                subcanvas.addLineBreak();
            }
        }
    }
}