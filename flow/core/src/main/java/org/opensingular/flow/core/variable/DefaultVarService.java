/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
