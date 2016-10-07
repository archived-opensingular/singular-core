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

import org.opensingular.flow.core.variable.type.VarTypeBoolean;
import org.opensingular.flow.core.variable.type.VarTypeDate;
import org.opensingular.flow.core.variable.type.VarTypeDouble;
import org.opensingular.flow.core.variable.type.VarTypeString;

public class DefaultVarService implements VarService {

    public static DefaultVarService DEFAULT_VAR_SERVICE = new DefaultVarService();

    @Override
    public VarService deserialize() {
        return DEFAULT_VAR_SERVICE;
    }

    @Override
    public VarDefinitionMap<?> newVarDefinitionMap() {
        return new DefaultVarDefinitionMap(this);
    }

    @Override
    public VarInstance newVarInstance(VarDefinition def) {
        return new DefaultVarInstance(def);
    }

    @Override
    public VarDefinition newDefinition(String ref, String name, VarType type) {
        return null;
    }

    @Deprecated
    @Override
    public VarDefinition newDefinitionString(String ref, String name, @Deprecated Integer tamanhoMaximo) {
        return new VarDefinitionImpl(ref, name, new VarTypeString(), false);
    }

    public VarDefinition newDefinitionString(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeString(), false);
    }

    /**
     * @deprecated não utilizar pois mistura apresentacao com definicao do fluxo
     */
    @Deprecated
    @Override
    public VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo) {
        return newDefinitionString(ref, name, tamanhoMaximo);
    }

    @Override
    public VarDefinition newDefinitionDate(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeDate(), false);
    }

    @Override
    public VarDefinition newDefinitionInteger(String ref, String name) {
        return null;
//        return new VarDefinitionImpl(ref, name, new VarTypeInteger(), false);
    }

    @Override
    public VarDefinition newDefinitionBoolean(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeBoolean(), false);
    }

    @Override
    public VarDefinition newDefinitionDouble(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeDouble(), false);
    }
}
