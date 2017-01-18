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
