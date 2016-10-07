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

/**
 * Representa a montagem (preparação) para execução de uma transanção a partir
 * de uma Task específica.
 *
 * @author Daniel C. Bordin
 */
public interface TransitionCall {

    /**
     * Retorna o mapa de parametros da chamada atual.
     */
    public VarInstanceMap<?> vars();

    /**
     * Executa a transição sobre a task sendo referenciada.
     */
    public void go();

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    public default TransitionCall addParamString(String ref, String value) {
        vars().addValorString(ref, value);
        return this;
    }

    /**
     * Set o valor na variável ou cria a variável senão existir.
     */
    public default TransitionCall addParam(String ref, VarType type, Object value) {
        vars().addValor(ref, type, value);
        return this;
    }

    /**
     * Set o valor na variável ou lança exception se a variável não existir na
     * transição.
     */
    public default TransitionCall setValor(String ref, Object value) {
        vars().setValor(ref, value);
        return this;
    }

}
