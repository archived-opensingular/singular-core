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

package org.opensingular.form.decorator.action;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.Test;

public class SIconTest {

    @Test
    public void testLineIcons() {
        assertEquals(setOf("icon-action-redo"), SIcon.resolve("action-redo").getIconCssClasses());
    }

    @Test
    public void testFontAwesome() {
        assertEquals(setOf("fa fa-trash-o"), SIcon.resolve("trash-o").getIconCssClasses());
    }

    @Test
    public void testGlyphIcons() {
        assertEquals(setOf("glyphicon glyphicon-piggy-bank"), SIcon.resolve("piggy-bank").getIconCssClasses());
    }

    @Test
    public void testUnresolved() {
        assertEquals(setOf("unresolvedicon"), SIcon.resolve("unresolvedicon").getIconCssClasses());
    }

    @Test
    public void testForced() {
        assertEquals(setOf("fa fa-check"), SIcon.resolve("fa fa-check").getIconCssClasses());
    }

    @Test
    public void defaultMethods() {
        assertEquals(setOf("bla"), SIcon.resolve("bla").getIconCssClasses());
    }

    private static Set<String> setOf(String... s) {
        final Pattern regex = Pattern.compile("\\s");
        return Stream.of(s)
            .flatMap(it -> regex.splitAsStream(it))
            .collect(toSet());
    }
}
