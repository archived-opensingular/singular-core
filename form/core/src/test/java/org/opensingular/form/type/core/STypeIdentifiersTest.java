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

package org.opensingular.form.type.core;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Enclosed.class)
public class STypeIdentifiersTest {

    @RunWith(value = Parameterized.class)
    public static class Valid {

        private PackageBuilder pkg;
        private String identifier;

        public Valid(String identifier){
            this.identifier = identifier;
        }

        @Before public void setUp(){
            SDictionary dict = SDictionary.create();
            pkg = dict.createNewPackage("test");
        }

        @Parameterized.Parameters(name = "{index}: {0}")
        public static Iterable<Object[]> data1() {
            return Arrays.asList(new Object[][]{
                    {"a"},{"_a"},{"_1"},{"_abc"},{"a1"}
            });
        }

        @Test
        public void valid() {
            pkg.createCompositeType(identifier);
        }
    }

    @RunWith(value = Parameterized.class)
    public static class Invalid {
        @Rule public final ExpectedException ex = ExpectedException.none();

        private PackageBuilder pkg;
        private String identifier;

        public Invalid(String identifier){
            this.identifier = identifier;
        }

        @Before public void setUp(){
            SDictionary dict = SDictionary.create();
            pkg = dict.createNewPackage("test");
        }

        @Parameterized.Parameters(name = "{index}: {0}")
        public static Iterable<Object[]> data1() {
            return Arrays.asList(new Object[][]{
                    {"1"},{"$"},{"@"}, {"não"}
            });
        }

        @Test
        public void invalid() {
            ex.expect(RuntimeException.class);
            pkg.createCompositeType(identifier);
        }
    }
}
