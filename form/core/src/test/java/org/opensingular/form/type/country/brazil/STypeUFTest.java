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

package org.opensingular.form.type.country.brazil;

import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.provider.ProviderContext;

import java.io.Serializable;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class STypeUFTest {


    @Test
    public void test() {
        SIComposite uf = SDictionary.create().newInstance(STypeUF.class);
        assertTrue(uf.isEmptyOfData());
        List values = uf.asAtrProvider().getProvider().load(ProviderContext.of(uf));
        assertFalse(values.isEmpty());
        uf.asAtrProvider().getConverter().fillInstance(uf, (Serializable) values.get(0));
        assertFalse(uf.isEmptyOfData());
    }

    @Test
    public void testSIUF() {
        SIComposite uf = SDictionary.create().newInstance(STypeUF.class);
        List values = uf.asAtrProvider().getProvider().load(ProviderContext.of(uf));
        assertFalse(values.isEmpty());
        uf.asAtrProvider().getConverter().fillInstance(uf, (Serializable) values.get(0));
        assertTrue(((SIUF) uf).getSigla().equalsIgnoreCase("AC"));
        assertTrue(((SIUF) uf).getNome().equalsIgnoreCase("Acre"));
    }
}