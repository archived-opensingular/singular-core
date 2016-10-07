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

package org.opensingular.flow.core.variable.type;

import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarDefinition;
import org.opensingular.flow.core.variable.VarType;

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
