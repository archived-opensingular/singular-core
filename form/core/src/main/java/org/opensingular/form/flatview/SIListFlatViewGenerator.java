/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.flatview;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.canvas.DocumentCanvas;
import org.opensingular.lib.commons.canvas.EmptyDocumentCanvas;
import org.opensingular.lib.commons.canvas.FormItem;

import javax.annotation.Nonnull;
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
            if(instance.getType().asAtr().getLabel() != null){
                titlePattern += context.getLabel();
            } else {
                titlePattern += elementsType.getNameSimple();
            }
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

    @Nonnull
    private Optional<FlatViewGenerator> getChildFlatViewGen(SInstance child) {
        return child.getAspect(ASPECT_FLAT_VIEW_GENERATOR);
    }
}