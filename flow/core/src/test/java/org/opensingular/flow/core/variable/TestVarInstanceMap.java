/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import org.junit.Test;
import org.opensingular.flow.core.SingularFlowException;

import java.util.Date;

import static org.junit.Assert.*;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.assertException;

/**
 * @author Daniel C. Bordin on 26/03/2017.
 */
public class TestVarInstanceMap {

    @Test
    public void empty() {
        VarInstanceMap empty = VarInstanceMap.empty();
        assertTrue(empty.isEmpty());
        assertNull(empty.getVariable("any"));
        assertEquals(0, empty.size());
        assertTrue(empty.asCollection().isEmpty());
        assertException(() -> empty.onValueChanged(null), SingularFlowException.class, "Método não suportado");
        assertException(() -> empty.addDefinition(null), SingularFlowException.class, "Método não suportado");
        assertException(() -> empty.getVarService(), SingularFlowException.class, "Método não suportado");
    }

    @Test
    public void testGetValue() {
        DefaultVarDefinitionMap defs = new DefaultVarDefinitionMap(DefaultVarService.DEFAULT_VAR_SERVICE);
        defs.addVariableString("ref", "Ref");

        VarInstanceMapImpl vars = new VarInstanceMapImpl(defs);

        assertNull(vars.getValue("ref"));
        assertEquals("x", vars.getValue("ref", "x"));

        assertException(() -> vars.setValue("f", "x"), SingularFlowException.class, "não está definida");
        assertException(() -> vars.getValue("f"), SingularFlowException.class, "não está definida");
        assertException(() -> vars.getValue("f", "x"), SingularFlowException.class, "não está definida");

        assertEquals(1, vars.stream().count());

        assertEquals("w", vars.getValueType("ref", String.class,  "w"));
        vars.setValue("ref", "y");
        assertException(() -> vars.getValueType("ref", Integer.class), SingularFlowException.class, "é do tipo");
    }

    @Test
    public void testDinamicAdd() {
        DefaultVarDefinitionMap defs = new DefaultVarDefinitionMap(DefaultVarService.DEFAULT_VAR_SERVICE);
        VarInstanceMapImpl vars = new VarInstanceMapImpl(defs);

        vars.addValueString("a", "x");
        assertEquals("x", vars.getValueString("a"));
        assertEquals("x", vars.getValueString("a", "y"));

        vars.addValueBoolean("b", Boolean.TRUE);
        assertEquals(Boolean.TRUE, vars.getValueBoolean("b"));
        assertEquals(Boolean.TRUE, vars.getValueBoolean("b", Boolean.FALSE));

        vars.addValueInteger("i", 10);
        assertEquals((Integer) 10, vars.getValueInteger("i"));
        assertEquals((Integer) 10, vars.getValueInteger("i", 20));

        Date now = new Date();
        vars.addValueDate("d", now);
        assertEquals(now, vars.getValueDate("d"));

        vars.addValueInteger("i", 10);
    }
}
