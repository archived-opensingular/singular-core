/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.flatview;

import org.opensingular.form.aspect.AspectRef;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

/**
 * Represents the capability of generating a flat representation (usually a html) of a {@link
 * org.opensingular.form.SInstance}.
 *
 * @author Daniel C. Bordin on 09/08/2017.
 */
public interface FlatViewGenerator {

    AspectRef<FlatViewGenerator> ASPECT_FLAT_VIEW_GENERATOR = new AspectRef<>(FlatViewGenerator.class, FlatViewGeneratorRegistry.class);

    /**
     * Write content to the supplied canvas
     * @param canvas the canvas
     * @param context the context
     */
    void writeOnCanvas(DocumentCanvas canvas, FlatViewContext context);
}
