/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.support.persistence.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.util.Loggable;

import static org.junit.Assert.*;

public class SqlUtilTest {


    @Test
    public void testeCustomSchemaName(){
        System.setProperty(SingularProperties.CUSTOM_SCHEMA_NAME, "ABOBRINHA");
        assertTrue(SqlUtil.isSingularSchema("ABOBRINHA"));
        assertFalse(SqlUtil.isSingularSchema(Constants.SCHEMA));
    }

    @Test
    public void testeDefaultSchemaName(){
        System.clearProperty(SingularProperties.CUSTOM_SCHEMA_NAME);
        assertTrue(SqlUtil.isSingularSchema(Constants.SCHEMA));
        assertFalse(SqlUtil.isSingularSchema("ABOBRINHA"));
    }

    @Test
    public void testeHasAllCrudOperations() {
        List<String> vals = Arrays.asList("SELECT", "UPDATE", "DELETE", "INSERT");

        assertTrue(SqlUtil.hasCompleteCrud(vals));
    }

    @Test
    public void testeCrudHasNullCollection() {
        assertFalse(SqlUtil.hasCompleteCrud(null));
    }

    @Test
    public void testeCrudValsIsEmpty() {
        assertFalse(SqlUtil.hasCompleteCrud(Collections.emptyList()));
    }

    @Test
    public void testeCrudIncompleteVals() {
        List<String> vals = Arrays.asList("SELECT", "UPDATE", null);

        assertFalse(SqlUtil.hasCompleteCrud(vals));
    }
}
