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

import org.junit.Test;

public class SupplierReloadableDetachableModelTest {

    @Test
    public void test() {
        boolean[] needsReload = { false };
        int[] n = { 0 };
        SupplierReloadableDetachableModel<Integer> model = new SupplierReloadableDetachableModel<Integer>(0, () -> ++n[0]) {
            @Override
            protected boolean needsReload(Integer cachedObject) {
                return super.needsReload(cachedObject) || needsReload[0];
            }
        };
        model.toString();

        assertTrue(model.isAttached());
        assertEquals(0, model.getObject().intValue());
        assertEquals(0, model.getObject().intValue());
        assertTrue(model.isAttached());

        model.detach();

        assertFalse(model.isAttached());
        assertEquals(1, model.getObject().intValue());
        assertEquals(1, model.getObject().intValue());
        assertTrue(model.isAttached());

        model.detach();

        assertFalse(model.isAttached());

        model.setObject(5);

        assertTrue(model.isAttached());
        assertEquals(5, model.getObject().intValue());

        needsReload[0] = true;
        assertEquals(2, model.getObject().intValue());
    }
}
