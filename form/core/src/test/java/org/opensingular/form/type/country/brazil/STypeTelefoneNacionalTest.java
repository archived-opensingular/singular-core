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

package org.opensingular.form.type.country.brazil;

import org.junit.Test;

import static org.junit.Assert.*;


public class STypeTelefoneNacionalTest {

    private STypeTelefoneNacional type = new STypeTelefoneNacional();

    @Test
    public void unformatTest() {
        assertEquals("06133725695", type.unformat("(061) 3372-5695"));
        assertEquals("00000000000", type.unformat("(00) 000000000"));
    }

    @Test
    public void testFormat() {
        assertEquals("(61) 3372-5695", type.format("06133725695"));
        assertEquals("(61) 3372-5695", type.format("6133725695"));
        assertEquals("(61) 98599-7893", type.format("061985997893"));
        assertEquals("(61) 98599-7893", type.format("(061) 98599-7893"));
        assertEquals("(61) 98599-7893", type.format("(061)98599-7893"));
        assertEquals("(61) 98599-7893", type.format("(061)985997893"));
        assertEquals("(61) 98599-7893", type.format("(061) 985997893"));
        assertEquals("(61) 98599-7893", type.format("61985997893"));
        assertEquals("(00) 00000-0000", type.format("00000000000"));
    }

    @Test
    public void extractDDDTest() {
        assertEquals("61", type.extractDDD("06133725695"));
        assertEquals("61", type.extractDDD("(061) 3372-5695"));
        assertEquals("61", type.extractDDD("(61) 3372-5695"));
        assertEquals("61", type.extractDDD("6133725695"));
        assertEquals("00", type.extractDDD("00000000000"));
    }

    @Test
    public void extractTelefoneTest() {
        assertEquals("3372-5695", type.extractNumber("06133725695"));
        assertEquals("3372-5695", type.extractNumber("(061) 3372-5695"));
        assertEquals("3372-5695", type.extractNumber("(61) 3372-5695"));
        assertEquals("98599-7893", type.extractNumber("061985997893"));
        assertEquals("98599-7893", type.extractNumber("61985997893"));
        assertEquals("8599", type.extractNumber("618599"));
        assertEquals("85", type.extractNumber("6185"));
        assertEquals("9859-9", type.extractNumber("6198599"));
        assertEquals("8599-789", type.extractNumber("618599789"));
        assertEquals("8599-7893", type.extractNumber("6185997893"));
        assertEquals("0000-0000", type.extractNumber("0000000000"));
    }


}