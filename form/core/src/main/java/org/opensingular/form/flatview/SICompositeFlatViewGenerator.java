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
