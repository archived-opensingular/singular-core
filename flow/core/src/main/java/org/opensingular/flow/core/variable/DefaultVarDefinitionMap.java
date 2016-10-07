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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class DefaultVarDefinitionMap implements VarDefinitionMap {

    private VarService varService;

    private final LinkedHashMap<String, VarDefinition> map = new LinkedHashMap<>();

    protected DefaultVarDefinitionMap(VarService varService){
        this.varService = varService;
    }

    @Override
    public Collection asCollection() {
        return map == null ? Collections.emptyList() : map.values();
    }

    @Override
    public VarDefinition getDefinition(String ref) {
        return map.get(ref);
    }

    @Override
    public VarInstanceMap<?> newInstanceMap() {
        return new VarInstanceMapImpl(getVarService());
    }

    @Override
    public VarDefinition addVariable(VarDefinition defVar) {
        if (map.containsKey(defVar.getRef())) {
            throw new RuntimeException("Já existe a definição '" + defVar.getRef() + "'");
        }
        map.put(defVar.getRef(), defVar);
        return defVar;
    }

    @Override
    public VarDefinition addVariable(String ref, String name, VarType varType) {
        return addVariable(new VarDefinitionImpl(ref, name, varType, false));
    }

    @Override
    public VarService getVarService() {
        return varService;
    }
}
