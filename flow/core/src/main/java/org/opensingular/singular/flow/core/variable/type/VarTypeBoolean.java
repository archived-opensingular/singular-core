/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.variable.type;

import org.opensingular.singular.flow.core.variable.VarDefinition;
import org.opensingular.singular.flow.core.variable.VarInstance;
import org.opensingular.singular.flow.core.variable.VarType;

public class VarTypeBoolean implements VarType {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toDisplayString(VarInstance varInstance) {
        return toDisplayString(varInstance.getValor(), varInstance.getDefinicao());
    }

    @Override
    public String toDisplayString(Object valor, VarDefinition varDefinition) {
        return String.valueOf(valor);
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Boolean.toString((Boolean) varInstance.getValor());
    }
}
