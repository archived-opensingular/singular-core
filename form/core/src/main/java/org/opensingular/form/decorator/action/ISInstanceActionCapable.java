/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.decorator.action;

import java.io.Serializable;

/**
 * Interface que Mappers podem implementar para sinalizarem seu suporte a ações sobre a instância.
 */
public interface ISInstanceActionCapable extends Serializable {

    /**
     * Registra um provider, com a ordem de prioridade máxima.
     */
    default void addSInstanceActionsProvider(ISInstanceActionsProvider provider) {
        this.addSInstanceActionsProvider(Integer.MIN_VALUE, provider);
    }
    
    /**
     * Registra um provider, com uma determinada ordem.
     */
    void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider);

}
