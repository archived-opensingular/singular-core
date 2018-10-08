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

package org.opensingular.internal.lib.support.spring.injection;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.internal.lib.commons.injection.SingularInjectionBeanNotFoundException;
import org.opensingular.internal.lib.commons.injection.SingularInjectionException;
import org.opensingular.internal.lib.commons.injection.SingularInjector;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.lib.commons.context.SingularContextSetup;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.opensingular.internal.lib.commons.test.SingularTestUtil.assertException;

/**
 * @author Daniel C. Bordin on 16/05/2017.
 */
public class SingularInjectorBySpringTest {

    private SingularInjector injector;

    private static Random random = new Random();

    @Before
    public void setUp() {
        createTestContext();
        injector = SingularSpringInjector.get();
    }

    @Test
    public void basic() {
        Basic1 basic1 = inject(new Basic1());
        Assert.assertNotNull(basic1.myMockService);
        Assert.assertEquals("A", basic1.fieldA);
        Assertions.assertThat(basic1.fieldA).isExactlyInstanceOf(String.class);
        Assertions.assertThat(basic1.myMockService.hashCode()).isNotNull();
        Assertions.assertThat(basic1.myMockService).isSameAs(basic1.myMockService);
        //The proxied equals doesn't work. Should it?
        Assertions.assertThat(basic1.myMockService).isNotEqualTo(basic1.myMockService);

        //Call a second time to test caches
        basic1 = inject(new Basic1());
        Assert.assertNotNull(basic1.myMockService);
        Assert.assertEquals("Y", basic1.myMockService.getResult());
        Assert.assertEquals("A", basic1.fieldA);
        Assertions.assertThat(basic1.fieldA).isExactlyInstanceOf(String.class);
        Assertions.assertThat(basic1.myMockService).isNotExactlyInstanceOf(MyMockService.class).isNotExactlyInstanceOf(
                MyMockInterfaceImpl.class);
    }

    private static class Basic1 {

        @Inject
        @Named("BeanA")
        private String fieldA;

        @Inject
        public MyMockService myMockService;

    }

    @Test
    @Ignore
    public void performance() {
        SingularTestUtil.performance("Basic1   warmUp", 2, () -> inject(new Basic1()));
        SingularTestUtil.performance("BasicOpt warmUp", 2, () -> inject(new Basic1Opt()));
        SingularTestUtil.performance("Basic4   warmUp", 2, () -> inject(new Basic4()));
        SingularTestUtil.performance("Basic1   run 1", 10, () -> inject(new Basic1()));
        SingularTestUtil.performance("BasicOpt run 1", 10, () -> inject(new Basic1Opt()));
        SingularTestUtil.performance("Basic4   run 1", 10, () -> inject(new Basic4()));
    }

    private static class Basic1Opt {

        @Inject
        @Named("BeanA")
        private Optional<String> fieldA;

        @Inject
        public Optional<MyMockService> myMockService;

    }

    @Test
    public void missingBean() {
        SingularTestUtil.assertException(() -> inject(new Basic2()), SingularInjectionBeanNotFoundException.class, "BeanX");

        SingularTestUtil.assertException(() -> inject(new Basic3()), SingularInjectionBeanNotFoundException.class,
                "MyMissingMockService");
    }

    private static class Basic2 {
        @Inject
        @Named("BeanX")
        private String fieldA;
    }

    private static class Basic3 {
        @Inject
        private MyMissingMockService fieldA;
    }

    @Test
    public void optional() {
        Basic4 basic4 = inject(new Basic4());
        Assert.assertTrue(basic4.myMockService.orElse(null) instanceof MyMockService);
        Assert.assertFalse(basic4.myMissingMockService.isPresent());
        Assert.assertFalse(basic4.fieldX.isPresent());
        Assert.assertEquals("A", basic4.fieldA.orElse(null));
    }

    private static class Basic4 {

        @Inject
        @Named("BeanA")
        private Optional<String> fieldA;

        @Inject
        @Named("BeanX")
        private Optional<String> fieldX;

        @Inject
        public Optional<MyMockService> myMockService;

        @Inject
        public Optional<MyMissingMockService> myMissingMockService;
    }

    @Test
    public void optionalMismatchedValue() {
        Basic5 basic5 = new Basic5();
        assertException(() -> inject(basic5), BeanNotOfRequiredTypeException.class, "is expected to be of type");
        assertNull(basic5.b);
    }

    private static class Basic5 {
        @Inject
        @Named("BeanA")
        private Optional<Boolean> b;
    }

    @Test
    public void testMultiInstancesWithDiferenteNames() {
        Basic6 basic6 = inject(new Basic6());
        assertNotNull(basic6.m);
    }

    private static class Basic6 {
        @Inject
        @Named("Multi1")
        private MyMultiMockService m;
    }

    @Test
    public void testConflitingInstances() {
        Basic7 basic7 = new Basic7();
        assertException(() -> inject(basic7), SingularInjectionException.class, " More than one bean");
        assertNull(basic7.m);
    }

    private static class Basic7 {
        @Inject
        private MyMultiMockService m;
    }

    @Test
    public void solvingAmbiguityByFieldName() {
        Basic8 basic8 = inject(new Basic8());
        assertNotNull(basic8.Multi1);
    }

    private static class Basic8 {
        @Inject
        @Named("Multi1")
        private MyMultiMockService Multi1;
    }

    @Test
    public void referenceAInterface() {
        Basic9 basic9 = inject(new Basic9());
        assertNotNull(basic9.my);
        assertEquals("W", basic9.my.getResult());
    }

    private static class Basic9 {
        @Inject
        private MyMockInterface my;
    }

    @Test
    public void serialization() {
        Basic10 original = inject(new Basic10());

        Basic10 newer = SingularIOUtils.serializeAndDeserialize(original, true);

        assertPosSerialization(original, newer, basic -> basic.my, true, bean -> bean.getV());
        assertPosSerialization(original, newer, basic -> basic.my2, true, bean -> bean.getV());
    }

    private <T,V, V2> void assertPosSerialization(T original, T newer, Function<T,V> valueReader, boolean expectedToBeProxied, Function<V,V2> internalValueReader) {
        V originalValue = valueReader.apply(original);
        V newerValue = valueReader.apply(newer);

        assertEquals(expectedToBeProxied, ILazyInitProxy.isProxied(originalValue));
        assertEquals(expectedToBeProxied, ILazyInitProxy.isProxied(newerValue));

        assertNotEquals(originalValue, originalValue);
        assertNotEquals(originalValue.hashCode(), newerValue.hashCode());

        V unProxiedOriginalValue = ILazyInitProxy.resolveProxy(originalValue);
        V unProxiedNewerValue = ILazyInitProxy.resolveProxy(newerValue);

        assertFalse(ILazyInitProxy.isProxied(unProxiedOriginalValue));
        assertFalse(ILazyInitProxy.isProxied(unProxiedNewerValue));

        assertSame(unProxiedOriginalValue, unProxiedNewerValue);

        assertEquals(internalValueReader.apply(originalValue), internalValueReader.apply(newerValue));
        assertEquals(internalValueReader.apply(unProxiedOriginalValue), internalValueReader.apply(unProxiedNewerValue));
        assertEquals(internalValueReader.apply(unProxiedOriginalValue), internalValueReader.apply(originalValue));
    }

    private static class Basic10 implements Serializable {

        @Inject
        private MyMockInterface my;

        @Inject
        private MyMockService my2;

    }

    @Test
    public void testInjectionWithoutApplicationContextAvailable() {
        SingularContextSetup.reset();
        assertFalse(ApplicationContextProvider.isConfigured());
        SingularInjector injector1 =  SingularSpringInjector.get();
        assertTrue(injector1 instanceof  SingularSpringInjector.SingularSpringInjectorProxy);

        injector1.inject(new Integer(1));
        injector1.inject(new MyMockInterfaceImpl());
        Basic1 basic1 = new Basic1();
        assertException(() -> injector1.inject(basic1), SingularInjectionException.class,
                "Foi encontrada essa solicitação injeção");
        assertNull(basic1.myMockService);

        createTestContext();
        assertTrue(ApplicationContextProvider.isConfigured());
        injector1.inject(basic1);
        assertNotNull(basic1.myMockService);

        SingularInjector injector2 =  SingularSpringInjector.get();
        assertFalse(injector2 instanceof  SingularSpringInjector.SingularSpringInjectorProxy);
        Basic1 basic1_2 = new Basic1();
        injector2.inject(basic1_2);
        assertNotNull(basic1_2.myMockService);
        assertEquals(basic1.myMockService.getV(), basic1_2.myMockService.getV());
    }

    private <T> T inject(T t) {
        injector.inject(t);
        return t;
    }

    private static ApplicationContext createTestContext() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.getBeanFactory().registerSingleton("BeanA", "A");
        ctx.getBeanFactory().registerSingleton(MyMockService.class.getName(), new MyMockService());
        ctx.getBeanFactory().registerSingleton("Multi1", new MyMultiMockService());
        ctx.getBeanFactory().registerSingleton("Multi2", new MyMultiMockService());
        ctx.getBeanFactory().registerSingleton(MyMockInterfaceImpl.class.getName(), new MyMockInterfaceImpl());
        ctx.refresh();
        new ApplicationContextProvider().setApplicationContext(ctx);
        return ctx;
    }

    public static class MyMockService {

        private final int v = random.nextInt();

        public String getResult() {
            return "Y";
        }

        public int getV() {
            return v;
        }

        @Override
        public int hashCode() {
            return v;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof MyMockService && v == ((MyMockService) obj).getV());
        }
    }

    public static class MyMissingMockService {

    }

    public static class MyMultiMockService {

    }

    public interface MyMockInterface {
        public String getResult();
        public int getV();
    }

    public static class MyMockInterfaceImpl implements MyMockInterface {

        private final int v = random.nextInt();

        @Override
        public String getResult() {
            return "W";
        }

        public int getV() {
            return v;
        }

        @Override
        public int hashCode() {
            return v;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof MyMockInterfaceImpl && v == ((MyMockInterfaceImpl) obj).getV());
        }
    }
}