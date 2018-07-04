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
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;

/** Converters a flow definition to a image representing the flow. */
public interface IFlowRenderer {

    /** Generates a byte array with PNG image representing the flow. */
    @Nonnull
    default byte[] generatePng(@Nonnull FlowDefinition<?> definition) {
        return generatePng(definition, (ExecutionHistoryForRendering) null);
    }

    /**
     * Generates a byte array with PNG image representing the flow and also highlights the transactions and task
     * executed if this information is available in the history parameter.
     */
    @Nonnull
    byte[] generatePng(@Nonnull FlowDefinition<?> definition, @Nullable ExecutionHistoryForRendering history);

    /**
     * Generates a PNG image to the output stream provided representing the flow.
     */
    default void generatePng(@Nonnull FlowDefinition<?> definition, @Nonnull OutputStream out) throws IOException {
        generatePng(definition, null, out);
    }

    /**
     * Generates a PNG image to the output stream provided representing the flow and also highlights the transactions
     * and task executed if this information is available in the history parameter.
     */
    void generatePng(@Nonnull FlowDefinition<?> definition, @Nullable ExecutionHistoryForRendering history,
            @Nonnull OutputStream out) throws IOException;


    /** Generates a diagram of the flow instance showing de process highlighting the executed tasks and transitions. */
    @Nonnull
    public default byte[] generateHistoryPng(@Nonnull FlowInstance flowInstance) {
        return generatePng(flowInstance.getFlowDefinition(), ExecutionHistoryForRendering.from(flowInstance));
    }
}
