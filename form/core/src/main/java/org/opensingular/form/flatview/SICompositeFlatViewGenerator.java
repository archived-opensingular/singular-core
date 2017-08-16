package org.opensingular.form.flatview;

import org.opensingular.form.SIComposite;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class SICompositeFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIComposite instance = context.getInstanceAs(SIComposite.class);
        DocumentCanvas subcanvas;
        if ((instance.getAllFields().size() == 1 && instance.asAtr().getLabel() == null) || context.isWithoutTitle()) {
            subcanvas = canvas;
        } else {
            canvas.addSubtitle(context.getLabelOrName());
            if (instance.getParent() == null) {
                subcanvas = canvas;
            } else {
                subcanvas = canvas.addChild();
            }
        }
        instance.getAllFields()
                .stream()
                .sorted((a, b) -> {
                    Integer _a = a.getType().isComposite() ? 1 : 0;
                    Integer _b = b.getType().isComposite() ? 1 : 0;
                    Integer compare = _a.compareTo(_b);
                    if (compare == 0) {
                        return Integer.compare(instance.getAllFields().indexOf(a), instance.getAllFields().indexOf(b));
                    }
                    return compare;
                })
                .forEach(child -> child.getAspect(ASPECT_FLAT_VIEW_GENERATOR)
                        .ifPresent(viewGenerator -> viewGenerator.writeOnCanvas(subcanvas, new FlatViewContext(child))));
    }
}
