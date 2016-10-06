/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.variable;

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
