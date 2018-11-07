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

package org.opensingular.flow.core.renderer;

import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;

/** Converters a flow definition to a image representing the flow. */
public interface IFlowRenderer {

    /** Creates a request of flow rendering for configuration before the call. */
    @Nonnull
    default RendererRequest createRequest(@Nonnull FlowDefinition<?> definition) {
        return new RendererRequest(this, definition);
    }

    /** Clones the flow rendering request. */
    @Nonnull
    default RendererRequest createRequest(@Nonnull RendererRequest requestToBeCopied) {
        RendererRequest req = createRequest(requestToBeCopied.getDefinition());
        req.setInstanceHistory(requestToBeCopied.getInstanceHistory());
        return req;
    }

    /** Generates a byte array with PNG image representing the flow. */
    @Nonnull
    byte[] generatePng(@Nonnull RendererRequest request);

    /** Generates a PNG image representing the flow to the output stream provided. */
    void generatePng(@Nonnull RendererRequest request, @Nonnull OutputStream out) throws IOException;

    /** Generates a diagram of the flow instance showing de process highlighting the executed tasks and transitions. */
    @Nonnull
    default byte[] generateHistoryPng(@Nonnull FlowInstance flowInstance) {
        RendererRequest req = createRequest(flowInstance.getFlowDefinition());
        req.setInstanceHistory(ExecutionHistoryForRendering.from(flowInstance));
        return generatePng(req);
    }
}
