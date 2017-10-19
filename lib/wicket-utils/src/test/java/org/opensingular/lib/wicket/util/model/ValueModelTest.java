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

package org.opensingular.lib.wicket.util.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

public class ValueModelTest {

    @Test
    public void test() {
        ValueModel<Integer> model = new ValueModel<>(1);

        assertEquals(1, model.getObject().intValue());
        assertEquals(Integer.class, model.getObjectClass());

        assertEquals(
            new HashSet<>(Arrays.asList(new ValueModel<>(1), new ValueModel<>(2))),
            new HashSet<>(Arrays.asList(new ValueModel<>(1), new ValueModel<>(2), new ValueModel<>(1))));

        assertNotNull(model.toString());
    }

    @Test
    public void detach() {
        SupplierReloadableDetachableModel<Integer> reloadable = new SupplierReloadableDetachableModel<>(1, () -> 1);

        assertTrue(reloadable.isAttached());
        new ValueModel<>(reloadable).detach();
        assertFalse(reloadable.isAttached());
    }

}
