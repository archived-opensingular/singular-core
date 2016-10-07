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

import org.opensingular.flow.core.SingularFlowException;

public abstract class AbstractVarInstanceMap<K extends VarInstance> implements VarInstanceMap<K> {

    private LinkedHashMap<String, K> variaveis = new LinkedHashMap<>();

    private final VarService varService;

    public AbstractVarInstanceMap(VarService varService) {
        this.varService = varService;
    }

    public AbstractVarInstanceMap(VarInstanceMap<?> instances) {
        varService = VarService.getVarService(instances);
        for (VarInstance var : instances) {
            addDefinicao(var.getDefinicao()).setValor(var.getValor());
        }
    }

    public AbstractVarInstanceMap(VarDefinitionMap<?> definitions) {
        varService = VarService.getVarService(definitions);
        for (VarDefinition def : definitions) {
            addDefinicao(def);
        }
    }

    @Override
    public K addDefinicao(VarDefinition def) {
        K v = newVarInstance(def);
        addInstance(v);
        return v;
    }

    protected final void addInstance(K varInstance) {
        if (variaveis == null) {
            variaveis = new LinkedHashMap<>();
        }
        variaveis.put(varInstance.getRef(), varInstance);
        if (wantToKnowAboutChanges()) {
            varInstance.setChangeListner(this);
        }
    }

    protected boolean wantToKnowAboutChanges() {
        return false;
    }

    protected K newVarInstance(VarDefinition def) {
        throw new SingularFlowException("Esse metodo ou addDefinicao() deve ser sobre escrito");
    }

    @Override
    public VarService getVarService() {
        return varService;
    }

    @Override
    public K getVariavel(String ref) {
        if (variaveis == null) {
            return null;
        }
        return variaveis.get(ref);
    }

    @Override
    public boolean isEmpty() {
        return variaveis == null || variaveis.isEmpty();
    }

    @Override
    public int size() {
        return variaveis == null ? 0 : variaveis.size();
    }

    @Override
    public Collection<K> asCollection() {
        return variaveis == null ? Collections.emptyList() : variaveis.values();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" + asCollection() + "]";
    }
}
