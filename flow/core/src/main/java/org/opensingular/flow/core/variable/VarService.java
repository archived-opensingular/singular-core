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

import java.io.Serializable;

public interface VarService extends Serializable {

    VarService deserialize();

    VarDefinitionMap<?> newVarDefinitionMap();

    /* TODO Verifica se ficou em uso no final, senão apagar */
    VarInstance newVarInstance(VarDefinition def);

    VarDefinition newDefinition(String ref, String name, VarType type);

    VarDefinition newDefinitionString(String ref, String name, Integer tamanhoMaximo);

    VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo);

    VarDefinition newDefinitionDate(String ref, String name);

    VarDefinition newDefinitionInteger(String ref, String name);

    VarDefinition newDefinitionBoolean(String ref, String name);

    VarDefinition newDefinitionDouble(String ref, String name);

    static VarService basic() {
        return DefaultVarService.DEFAULT_VAR_SERVICE;
    }

    static VarService getVarService(VarServiceEnabled source) {
        VarService s = source.getVarService();
        if (s == null) {
            throw new UnsupportedOperationException("Falta implementar VarServiceBasic.class");
        }
        return s.deserialize();
    }
}
