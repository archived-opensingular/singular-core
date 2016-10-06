/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.variable;

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
