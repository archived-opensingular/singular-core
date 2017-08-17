package org.opensingular.form.flatview;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SICompositeFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIComposite instance = context.getInstanceAs(SIComposite.class);
        DocumentCanvas subcanvas;
        if (isFlatView(context, instance)) {
            subcanvas = canvas;
        } else {
            canvas.addSubtitle(context.getLabel());
            if (instance.getParent() == null) {
                subcanvas = canvas;
            } else {
                subcanvas = canvas.addChild();
            }
        }
        List<SInstance> fields = instance.getAllFields()
                .stream()
                .sorted(Comparator.comparing(this::isChildWithSessionBreaker)).collect(Collectors.toList());
        int rowCount = 0;
        for (SInstance child : fields) {
            rowCount += child.asAtrBootstrap().getColPreference();
            if (rowCount > 12 || isChildWithSessionBreaker(child)) {
                rowCount = 0;
                subcanvas.addLineBreak();
            }
            Optional<FlatViewGenerator> aspect = child.getAspect(ASPECT_FLAT_VIEW_GENERATOR);
            aspect.ifPresent(flatViewGenerator -> flatViewGenerator.writeOnCanvas(subcanvas, new FlatViewContext(child)));
        }
    }

    private boolean isFlatView(FlatViewContext context, SIComposite instance) {
        return instance.asAtr().getLabel() == null || context.isWithoutTitle();
    }

    private Boolean isChildWithSessionBreaker(SInstance a) {
        return (a.asAtrBootstrap().getColPreference(12) == 12 && a.getType().isComposite()
                && a.asAtr().getLabel() != null) || a.getType().isList();
    }
}
