package org.opensingular.form.flatview;

import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.EmptyDocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SIListFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIList<?> instance = context.getInstanceAs(SIList.class);
        SType<?> elementsType = instance.getElementsType();
        if (elementsType.isComposite() || elementsType.isList()) {
            doWriteCompositeOrListOnCanvas(canvas, context, instance, elementsType);
        } else {
            doWriteSimpleOnCanvas(canvas, context, instance);
        }
    }

    private void doWriteSimpleOnCanvas(DocumentCanvas canvas, FlatViewContext context, SIList<?> instance) {
        if (!context.isWithoutTitle()) {
            canvas.addSubtitle(context.getLabel());
        }
        List<String> listValues = new ArrayList<>();
        for (SInstance child : instance) {
            getChildFlatViewGen(child).ifPresent(flatViewGenerator -> flatViewGenerator.writeOnCanvas(new EmptyDocumentCanvas() {
                @Override
                public void addFormItem(FormItem formItem) {
                    listValues.add(formItem.getValue());
                }
            }, new FlatViewContext(child)));
        }
        canvas.addList(listValues);
    }

    private void doWriteCompositeOrListOnCanvas(DocumentCanvas canvas, FlatViewContext context, SIList<?> instance, SType<?> elementsType) {
        int index = 1;
        String titlePattern = "";
        boolean prefixWithChildTitle = true;
        if (elementsType.asAtr().getLabel() == null) {
            titlePattern += context.getLabel();
            prefixWithChildTitle = false;
        }
        titlePattern += " (%s de " + instance.size() + ")";
        for (SInstance child : instance) {
            FlatViewContext childContext = new FlatViewContext(child);
            String title = "";
            if (prefixWithChildTitle) {
                title = childContext.getLabel();
            }
            title += titlePattern;
            canvas.addSubtitle(String.format(title, index++));
            Optional<FlatViewGenerator> aspect = getChildFlatViewGen(child);
            if (aspect.isPresent()) {
                aspect.get().writeOnCanvas(canvas, childContext);
                canvas.addLineBreak();
            }
        }
    }

    @NotNull
    private Optional<FlatViewGenerator> getChildFlatViewGen(SInstance child) {
        return child.getAspect(ASPECT_FLAT_VIEW_GENERATOR);
    }
}