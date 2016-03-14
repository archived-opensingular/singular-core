/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.variable.type;

import br.net.mirante.singular.flow.core.variable.VarDefinition;
import br.net.mirante.singular.flow.core.variable.VarInstance;
import br.net.mirante.singular.flow.core.variable.VarType;

import java.util.Locale;

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
