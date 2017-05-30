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

package org.opensingular.internal.lib.commons.injection;

import org.junit.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.assertException;

/**
 * @author Daniel C. Bordin on 16/05/2017.
 */
public class SingularInjectorTest {

    @Test
    public void basic() {
        Simple simple = inject(new Simple(null));
        assertEquals(simple.a, "A");
        assertEquals(simple.n, Integer.valueOf(1));
        assertEquals(simple.n2, null);
        assertEquals(simple.i, Integer.valueOf(2));

        simple = inject(new Simple("C"));
        assertEquals(simple.a, "C");
    }

    private static class Simple {
        @Inject
        protected String a;

        @Inject
        protected Number n;

        protected Number n2;

        @Inject
        protected Integer i;

        public Simple(String valueA) {
            a = valueA;
        }
    }

    @Test
    public void withDerivation() {
        SimpleExtended simple = inject(new SimpleExtended());
        assertEquals(simple.a, "A");
        assertEquals(simple.n, Integer.valueOf(1));
        assertEquals(simple.n2, null);
        assertEquals(simple.i, Integer.valueOf(2));
        assertEquals(simple.s, "X");
    }

    private static class SimpleExtended extends Simple {

        @Inject
        @Named("beanA")
        private String s;

        public SimpleExtended() {
            super(null);
        }
    }

    @Test
    public void objectWithOutInject() {
        Boolean b = inject(Boolean.TRUE);
    }

    @Test
    public void notFoundInject() {
        Simple2 simple2 = new Simple2();
        assertException(() -> inject(simple2), SingularBeanNotFoundException.class);
        assertNull(simple2.a);
    }

    private static class Simple2 {
        @Inject
        private Boolean a;
    }

    @Test
    public void injectOptional() {
        Simple3 simple3 = inject(new Simple3());
        assertFalse(simple3.b.isPresent());
        assertEquals("A", simple3.s1.orElse(null) );
        assertEquals("X", simple3.s2.orElse(null));
        assertFalse(simple3.s3.isPresent());
    }

    private static class Simple3 {
        @Inject
        private Optional<Boolean> b;

        @Inject
        private Optional<String> s1;

        @Inject
        @Named("beanA")
        private Optional<String> s2;

        @Inject
        @Named("beanX")
        private Optional<String> s3;
    }

    @Test
    public void injectOptionalMismatchedType() {
        Simple4 simple4 = new Simple4();
        assertException(() -> inject(simple4),SingularInjectionException.class, "tipo do Optional incompatível");
        assertNull(simple4.b);
    }

    private static class Simple4 {
        @Inject
        @Named("beanA")
        private Optional<Boolean> b;
    }

    private <T> T inject(T t) {
        create().inject(t);
        return t;
    }


    @Test
    public void injectWithoutBeanProviderAvailable() {
        SingularInjector injector = SingularInjector.getEmptyInjector();
        injector.inject(new Integer(1));

        assertException(() -> injector.inject(new Simple3()), SingularInjectionNotConfiguredException.class);
        assertException(() -> injector.inject(new Simple2()), SingularInjectionNotConfiguredException.class);
    }

    private void assertEmptyOptional(Optional<?> opt) {
        assertNotNull(opt);
        assertFalse(opt.isPresent());
    }

    private SingularInjector create() {
        return new SingularInjectorImpl(new SingularFieldValueFactoryMock());
    }

    private static class SingularFieldValueFactoryMock implements SingularFieldValueFactory {

        private final Map<Class<?>, Object> values = new LinkedHashMap<>();

        private final Map<String, Object> byName = new HashMap();

        public SingularFieldValueFactoryMock() {
            values.put(String.class, "A");
            values.put(Integer.class, 2);
            values.put(Number.class, 1);

            byName.put("beanA", "X");
            byName.put("beanB", Integer.valueOf(10));
        }

        @Override
        public Object getFieldValue(@Nonnull FieldInjectionInfo fieldInfo, @Nonnull Object fieldOwner) {
            if (fieldInfo.hasBeanName()) {
                return byName.get(fieldInfo.getBeanName());
            }
            for (Map.Entry<Class<?>, Object> entry : values.entrySet()) {
                if (entry.getKey() == fieldInfo.getType()) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }
}