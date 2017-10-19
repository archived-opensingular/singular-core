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

package org.opensingular.flow.core;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Reresenta uma referência a {@link FlowDefinition} que pode ser serializada de modo
 * que não provoque um serialização de toda a definição. Posteriormente a ser
 * restaurada, recarrar a instância de {@link FlowDefinition} sob demanda.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefFlowDefinition implements Serializable, Supplier<FlowDefinition<?>> {

    private transient FlowDefinition<?> flowDefinition;

    @Nonnull
    protected abstract FlowDefinition<?> reload();

    @Override
    public final FlowDefinition<?> get() {
        if (flowDefinition == null) {
            flowDefinition = Objects.requireNonNull(reload(),
                    () -> getClass().getName() + ".reload() retornou null");
        }
        return flowDefinition;
    }

    @Nonnull
    public static RefFlowDefinition of(@Nonnull Class<? extends FlowDefinition<?>> flowDefinitionClass) {
        Objects.requireNonNull(flowDefinitionClass);
        return FlowDefinitionCache.getDefinition(flowDefinitionClass).getSerializableReference();
    }

    @Nonnull
    public static RefFlowDefinition of(@Nonnull FlowDefinition<?> definition) {
        return Objects.requireNonNull(definition).getSerializableReference();
    }
}
