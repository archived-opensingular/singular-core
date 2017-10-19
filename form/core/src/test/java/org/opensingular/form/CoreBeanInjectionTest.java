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

package org.opensingular.form;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.document.MockServiceRegistry;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.io.TestFormSerializationUtil;
import org.opensingular.internal.lib.commons.injection.SingularInjectionBeanNotFoundException;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Daniel C. Bordin on 21/05/2017.
 */
@RunWith(Parameterized.class)
public class CoreBeanInjectionTest extends TestCaseForm {


    public CoreBeanInjectionTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void before() {
        MockServiceRegistry service = new MockServiceRegistry();
        service.registerBean(MyBean.class, new MyBean());
        ServiceRegistryLocator.setup(service);
    }

    @After
    public void clean() {
        ServiceRegistryLocator.setup(new ServiceRegistryLocator());
    }

    @Test(expected = SingularInjectionBeanNotFoundException.class)
    public void injectionWithoutConfiguration1() {
        ServiceRegistryLocator.setup(new MockServiceRegistry());
        createTestDictionary().getType(TypeWithInjectionTest.class);
    }

    @Test(expected = SingularInjectionBeanNotFoundException.class)
    public void injectionWithoutConfiguration2() {
        ServiceRegistryLocator.setup(new MockServiceRegistry());
        TypeWithInjectionTest2 type = createTestDictionary().getType(TypeWithInjectionTest2.class);
        type.newInstance();
    }


    @Test
    public void injection1() {
        SDictionary dictionary = SDictionary.create();

        TypeWithInjectionTest type = dictionary.getType(TypeWithInjectionTest.class);
        assertNotNull(type.myBean);

        SInstanceWithInjection instance = type.newInstance();
        assertNotNull(instance.myBean);
    }

    @Test
    public void injection2() {
        RefType refType = RefType.of(() -> SDictionary.create().getType(TypeWithInjectionTest2.class));

        TypeWithInjectionTest2 type = (TypeWithInjectionTest2) refType.get();

        SDocumentFactory        factory  = SDocumentFactory.empty();
        SInstanceWithInjection2 instance = (SInstanceWithInjection2) factory.createInstance(refType);
        assertNotNull(instance.myBean);

        assertSerialization(instance);
    }

    @Test
    public void serialization() {
        RefType refType = RefType.of(() -> {
            SDictionary dictionary = SDictionary.create();
            return dictionary.getType(TypeWithInjectionTest.class);
        });
        SInstanceWithInjection instance = (SInstanceWithInjection) SDocumentFactory.empty().createInstance(refType);

        assertSerialization(instance);
    }

    private void assertSerialization(SInstanceWithInjection instance) {
        assertNotNull(((TypeWithInjectionTest) instance.getType()).myBean);
        assertNotNull(instance.myBean);

        SInstanceWithInjection instance2 = (SInstanceWithInjection) TestFormSerializationUtil.serializeAndDeserialize(instance);
        assertNotNull(((TypeWithInjectionTest) instance2.getType()).myBean);
        assertNotNull(instance2.myBean);
        assertEquals(instance.myBean.getV(), instance2.myBean.getV());
    }

    private void assertSerialization(SInstanceWithInjection2 instance) {
        assertNotNull(instance.myBean);
        SInstanceWithInjection2 instance2 = (SInstanceWithInjection2) TestFormSerializationUtil.serializeAndDeserialize(instance);
        assertNotNull(instance2.myBean);
        assertEquals(instance.myBean.getV(), instance2.myBean.getV());
    }


    @SInfoPackage(name = "test.inejctions")
    public static class SPackageInjections extends SPackage {

    }

    @SInfoType(spackage = SPackageInjections.class)
    public static class TypeWithInjectionTest extends STypeComposite<SInstanceWithInjection> {

        @Inject
        private MyBean myBean;

        public TypeWithInjectionTest() {
            super(SInstanceWithInjection.class);
        }

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            addFieldString("name");
        }
    }

    public static class SInstanceWithInjection extends SIComposite {

        @Inject
        private MyBean myBean;

    }

    @SInfoType(spackage = SPackageInjections.class)
    public static class TypeWithInjectionTest2 extends STypeComposite<SInstanceWithInjection2> {

        public TypeWithInjectionTest2() {
            super(SInstanceWithInjection2.class);
        }

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            addFieldString("name");
        }
    }

    public static class SInstanceWithInjection2 extends SIComposite {

        @Inject
        private MyBean myBean;

    }

    public static class MyBean {

        private final long v = RandomUtils.nextLong(0, Long.MAX_VALUE);

        public long getV() {
            return v;
        }
    }
}
