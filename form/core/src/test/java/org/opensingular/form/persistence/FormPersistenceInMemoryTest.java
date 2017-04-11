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

package org.opensingular.form.persistence;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.assertException;

/**
 * @author Daniel C. Bordin on 06/04/2017.
 */
public class FormPersistenceInMemoryTest extends AbstractFormPersistenceTest {

    @Override
    public AbstractFormPersistence createPersistence() {
        return new FormPersistenceInMemory();
    }

    @Test
    public void testEmptyRepository() {
        AbstractFormPersistence p = createPersistence();
        assertEquals(0, p.countAll());
        assertEquals(0, p.loadAll().size());
        assertEquals(0, p.loadAll(0, 10).size());

        assertException(()->p.loadAll(-10, -5), IndexOutOfBoundsException.class);
        assertException(()->p.loadAll(10, 20), IndexOutOfBoundsException.class);
        assertException(()->p.loadAll(20, 10), IndexOutOfBoundsException.class);

        assertException(()-> p.load(p.keyFromObject(10)), SingularFormNotFoundException.class);
        p.delete(p.keyFromObject(10));
        assertFalse(p.loadOpt(p.keyFromObject(20)).isPresent());
    }

    private void createPersistenceWith(int qtd) {
        AbstractFormPersistence p = createPersistence();
        p.createInstance();

    }
}