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

import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.variable.VarType;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Representa a montagem (preparação) para execução de uma transanção a partir
 * de uma Task específica.
 *
 * @author Daniel C. Bordin
 */
public final class TransitionCall {

    private final TransitionRef transition;

    private VarInstanceMap<?> vars;

    TransitionCall(@Nonnull TransitionRef transition) {
        this.transition = Objects.requireNonNull(transition);
    }

    /**
     * Retorna o mapa de parametros da chamada atual.
     */
    @Nonnull
    public VarInstanceMap<?> vars() {
        if (vars == null) {
            vars = transition.newTransationParameters();
        }
        return vars;
    }

    /**
     * Executa a transição sobre a task sendo referenciada.
     */
    public void go() {
        FlowEngine.executeTransition(transition.getOriginTaskInstance(), transition.getTransition(), vars);
    }

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    @Nonnull
    public TransitionCall addParamString(String ref, String value) {
        vars().addValueString(ref, value);
        return this;
    }

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    @Nonnull
    public TransitionCall addParam(String ref, VarType type, Object value) {
        vars().addValue(ref, type, value);
        return this;
    }

    /**
     * Set o valor na variável ou lança exception se a variável não existir na
     * transição.
     */
    @Nonnull
    public TransitionCall setValue(String ref, Object value) {
        vars().setValue(ref, value);
        return this;
    }
}
