/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

/**
 * Representa um chamada (execução de açõe no fluxo) que pode receber parametros para sua execução.
 *
 * @author Daniel C. Bordin on 20/03/2017.
 */
public abstract class CallWithParameters<SELF extends CallWithParameters<SELF>> {

    private VarInstanceMap<?> parameters;

    /**
     * Retorna o mapa de parametros da chamada atual.
     */
    @Nonnull
    public VarInstanceMap<?> parameters() {
        if (parameters == null) {
            parameters = newParameters();
        }
        return parameters;
    }

    /** Cria um novo conjunto de paramétros para a chamada. */
    protected abstract VarInstanceMap<?> newParameters();

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    @Nonnull
    public SELF addParamString(String ref, String value) {
        parameters().addValueString(ref, value);
        return (SELF) this;
    }

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    @Nonnull
    public SELF addParam(String ref, VarType type, Object value) {
        parameters().addValue(ref, type, value);
        return (SELF) this;
    }

    /**
     * Set o valor na variável ou lança exception se a variável não existir na
     * transição.
     */
    @Nonnull
    public SELF setValue(String ref, Object value) {
        parameters().setValue(ref, value);
        return (SELF) this;
    }
}
