package org.opensingular.form.flatview;

import org.opensingular.form.SIComposite;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class SICompositeFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIComposite instance = context.getInstanceAs(SIComposite.class);
        canvas.addTitle(context.getLabelOrName());
        DocumentCanvas subcanvas;
        if (instance.getParent() == null) {
            subcanvas = canvas;
        } else {
            subcanvas = canvas.newChild();
        }
        instance.getAllFields()
                .stream()
                .sorted((a, b) -> {
                    Integer _a = a.getType().isComposite() ? 1 : 0;
                    Integer _b = b.getType().isComposite() ? 1 : 0;
                    return _a.compareTo(_b);
                })
                .forEach(child -> {
                    child.getAspect(ASPECT_FLAT_VIEW_GENERATOR)
                            .ifPresent(viewGenerator -> viewGenerator.writeOnCanvas(subcanvas, new FlatViewContext(child)));
                });
    }
}
