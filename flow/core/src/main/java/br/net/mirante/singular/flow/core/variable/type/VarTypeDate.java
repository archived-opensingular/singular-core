/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.variable.type;

import br.net.mirante.singular.flow.core.variable.VarDefinition;
import br.net.mirante.singular.flow.core.variable.VarInstance;
import br.net.mirante.singular.flow.core.variable.VarType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VarTypeDate implements VarType {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
        Date date = (Date) valor;
        if (new SimpleDateFormat("hh:mm:ss").format(date).equals("00:00:00")) {
            return formatter.format(date);
        } else {
            return timeFormatter.format(date);
        }
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Integer.toString((Integer) varInstance.getValor());
    }


}
