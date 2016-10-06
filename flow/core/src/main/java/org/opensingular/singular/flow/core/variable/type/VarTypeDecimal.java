/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core.variable.type;

import org.opensingular.singular.flow.core.variable.VarDefinition;
import org.opensingular.singular.flow.core.variable.VarInstance;
import org.opensingular.singular.flow.core.variable.VarType;

import java.math.BigDecimal;

public class VarTypeDecimal implements VarType {

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
        return ((BigDecimal) valor).toPlainString();
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        BigDecimal  valor = (BigDecimal) varInstance.getValor();
        if (valor == null){
            return null;
        }
        return valor.toPlainString();
    }

}
