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

import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.variable.VarService;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Representa um chamada (execução de açõe no fluxo) que pode receber parametros para sua execução.
 *
 * @author Daniel C. Bordin on 20/03/2017.
 */
public abstract class CallWithParameters<SELF extends CallWithParameters<SELF>>
        implements VarInstanceMap<VarInstance, SELF> {

    private VarInstanceMap<?,?> parameters;

    /**
     * Retorna o mapa de parametros da chamada atual.
     */
    @Nonnull
    public VarInstanceMap<?,?> getParameters() {
        if (parameters == null) {
            parameters = newParameters();
        }
        return parameters;
    }

    /** Cria um novo conjunto de paramétros para a chamada. */
    protected abstract VarInstanceMap<?,?> newParameters();

    @Override
    public VarService getVarService() {
        return getParameters().getVarService();
    }

    @Override
    public VarInstance getVariable(String ref) {
        return getParameters().getVariable(ref);
    }

    @Override
    public Collection<VarInstance> asCollection() {
        return (Collection<VarInstance>) getParameters().asCollection();
    }

    @Override
    public VarInstance addDefinition(VarDefinition def) {
        return getParameters().addDefinition(def);
    }

    @Override
    public int size() {
        return getParameters().size();
    }

    @Override
    public boolean isEmpty() {
        return getParameters().isEmpty();
    }

    @Override
    public void onValueChanged(VarInstance changedVar) {
        getParameters().onValueChanged(changedVar);
    }
}
