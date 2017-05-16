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

package org.opensingular.flow.core.variable;

import org.opensingular.flow.core.SingularFlowException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;

public class DefaultVarDefinitionMap implements VarDefinitionMap {

    @Nonnull
    private VarService varService;

    private final LinkedHashMap<String, VarDefinition> map = new LinkedHashMap<>();

    protected DefaultVarDefinitionMap(@Nonnull VarService varService){
        this.varService = Objects.requireNonNull(varService);
    }

    @Override
    @Nonnull
    public Collection asCollection() {
        return map.values();
    }

    @Override
    public VarDefinition getDefinition(String ref) {
        return map.get(ref);
    }

    @Override
    @Nonnull
    public VarInstanceMap<?,?> newInstanceMap() {
        return new VarInstanceMapImpl(this);
    }

    @Override
    @Nonnull
    public VarDefinition addVariable(@Nonnull VarDefinition defVar) {
        if (map.containsKey(defVar.getRef())) {
            throw new SingularFlowException("Já existe a definição '" + defVar.getRef() + "'");
        }
        map.put(defVar.getRef(), defVar);
        return defVar;
    }

    @Override
    @Nonnull
    public VarDefinition addVariable(@Nonnull String ref, String name, @Nonnull VarType varType) {
        return addVariable(new VarDefinitionImpl(ref, name, varType, false));
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public VarDefinition addVariableCustom(@Nonnull String ref, @Nonnull String name, @Nonnull Class variableClass) {
        VarDefinition defVar = getVarService().newDefinitionCustom(ref, name, variableClass);
        return addVariable(defVar);
    }

    @Override
    @Nonnull
    public VarService getVarService() {
        return varService;
    }
}
