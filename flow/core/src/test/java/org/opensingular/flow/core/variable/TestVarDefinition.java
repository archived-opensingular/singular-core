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
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Daniel C. Bordin on 26/03/2017.
 */
public class TestVarDefinition {

    @Test
    public void testConversionException() {
        DefaultVarDefinitionMap defs = new DefaultVarDefinitionMap(DefaultVarService.DEFAULT_VAR_SERVICE);
        VarDefinition varInteger = defs.addVariableInteger("varInteger");
        VarDefinition varDate = defs.addVariableDate("varDate");
        VarDefinition varBoolean = defs.addVariableBoolean("varBoolean");
        VarDefinition varBigDecimal = defs.addVariableBigDecimal("varBigDecimal");

        assertException(() -> varInteger.convert("xx"));
        assertException(() -> varInteger.fromPersistenceString("xx"));
        assertException(() -> varDate.convert("xx"));
        assertException(() -> varDate.fromPersistenceString("xx"));
        assertException(() -> varBoolean.convert(10));
        assertException(() -> varBigDecimal.convert("xx"));
        assertException(() -> varBigDecimal.fromPersistenceString("xx"));
    }

    private void assertException(SingularTestUtil.RunnableEx code) {
        SingularTestUtil.assertException(code, SingularFlowConvertingValueException.class);
    }
}
