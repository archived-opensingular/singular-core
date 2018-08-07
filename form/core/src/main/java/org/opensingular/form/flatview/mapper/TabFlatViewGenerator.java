/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.flatview.mapper;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.view.SViewTab;
import org.opensingular.form.view.ViewResolver;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class TabFlatViewGenerator extends AbstractFlatViewGenerator {

    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        canvas.addSubtitle(context.getLabel());
        SIComposite instance = (SIComposite) context.getInstance();
        SViewTab viewTab = (SViewTab) ViewResolver.resolveView(instance.getType());
        for (SViewTab.STab tab : viewTab.getTabs()) {
            canvas.addSubtitle(tab.getTitle());
            for (String path : tab.getTypesNames()) {
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