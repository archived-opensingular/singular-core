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

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Reresenta uma referência a ProcessDefinition que pode ser serializada de modo
 * que não provoque um serialização de toda a definição. Posteriormente a ser
 * restaurada, recarrar a instância de ProcessDefinition sob demanda.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefProcessDefinition implements Serializable, Supplier<ProcessDefinition<?>> {

    private transient ProcessDefinition<?> processDefinition;

    protected abstract ProcessDefinition<?> reload();

    @Override
    public final ProcessDefinition<?> get() {
        if (processDefinition == null) {
            processDefinition = reload();
        }
        return processDefinition;
    }

    public static RefProcessDefinition of(Class<? extends ProcessDefinition<?>> processDefinitionClass) {
        return ProcessDefinitionCache.getDefinition(processDefinitionClass).getSerializableReference();
    }

    public static RefProcessDefinition of(ProcessDefinition<?> definition) {
        return definition.getSerializableReference();
    }
}
