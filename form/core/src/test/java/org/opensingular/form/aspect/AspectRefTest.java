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

package org.opensingular.form.aspect;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.util.STypeEMail;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Daniel C. Bordin on 10/08/2017.
 */
@RunWith(Parameterized.class)
public class AspectRefTest extends TestCaseForm {

    public AspectRefTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void basicTest() {
        SDictionary dictionary = createTestDictionary();

        STypeString typeString = dictionary.getType(STypeString.class);
        Optional<MyInterface> aspect = typeString.getAspect(ASPECT_MY_INTERFACE);
        Assert.assertEquals("string", aspect.orElse(null).get());

        assertAspectResult(dictionary, STypeSimple.class, "simple");
        assertAspectResult(dictionary, STypeInteger.class, "simple");
        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeEMail.class, "string");
        assertAspectResult(dictionary, SType.class, null);
        assertAspectResult(dictionary, STypeComposite.class, null);
        assertAspectResult(dictionary, STypeAttachment.class, null);
    }

    @Test
    public void customAspectDirectSTypeClass() {
        SDictionary dictionary = createTestDictionary();

        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeEMail.class, "string");
        assertAspectResult(dictionary, STypeAttachment.class, null);
        assertAspectResult(dictionary, STypeComposite.class, null);

        dictionary.getType(STypeAttachment.class).setAspectFixImplementation(ASPECT_MY_INTERFACE, () -> "attachment");
        assertAspectResult(dictionary, STypeAttachment.class, "attachment");
        assertAspectResult(dictionary, STypeComposite.class, null);

        dictionary.getType(STypeComposite.class).setAspectFixImplementation(ASPECT_MY_INTERFACE, (MyInterface) (() -> "composite"));
        assertAspectResult(dictionary, STypeAttachment.class, "attachment");
        assertAspectResult(dictionary, STypeComposite.class, "composite");

        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeEMail.class, "string");

        dictionary.getType(STypeEMail.class).setAspectFixImplementation(ASPECT_MY_INTERFACE, () -> "email");
        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeEMail.class, "email");
    }

    @Test
    public void customAspectInCompositeFiled() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pack = dictionary.createNewPackage("teste");

        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeComposite.class, null);

        STypeComposite<SIComposite> block = pack.createCompositeType("block");
        STypeString name = block.addFieldString("name");
        STypeString company = block.addFieldString("company");
        block.setAspectFixImplementation(ASPECT_MY_INTERFACE, () -> "block");
        name.setAspectFixImplementation(ASPECT_MY_INTERFACE, () -> "name");

        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeComposite.class, null);
        assertAspectResult(block, "block");
        assertAspectResult(name, "name");
        assertAspectResult(company, "string");

        STypeComposite<SIComposite> block2 = pack.createType("block2", block);
        block2.getField("company").setAspectFixImplementation(ASPECT_MY_INTERFACE, () -> "company2");

        assertAspectResult(dictionary, STypeString.class, "string");
        assertAspectResult(dictionary, STypeComposite.class, null);
        assertAspectResult(block, "block");
        assertAspectResult(name, "name");
        assertAspectResult(company, "string");
        assertAspectResult(block2, "block");
        assertAspectResult(block2.getField("name"), "name");
        assertAspectResult(block2.getField("company"), "company2");
    }

    @Test
    public void testAspectWithoutDefaultRegister() {
        SDictionary dictionary = createTestDictionary();

        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeString.class, null);
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeEMail.class, null);
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeAttachment.class, null);
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeComposite.class, null);

        dictionary.getType(STypeAttachment.class).setAspectFixImplementation(ASPECT_MY_INTERFACE2, () -> "attachment");
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeAttachment.class, "attachment");
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeComposite.class, null);

        dictionary.getType(STypeComposite.class).setAspectFixImplementation(ASPECT_MY_INTERFACE2, (MyInterface2) (() -> "composite"));
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeAttachment.class, "attachment");
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeComposite.class, "composite");

        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeString.class, null);
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeEMail.class, null);

        dictionary.getType(STypeString.class).setAspectFixImplementation(ASPECT_MY_INTERFACE2, () -> "string");
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeString.class, "string");
        assertAspectResult(ASPECT_MY_INTERFACE2, dictionary, STypeEMail.class, "string");
    }

    static <T extends SType<?>> void assertAspectResult(SDictionary dictionary, Class<T> typeClass,
            String expectedResult) {
        assertAspectResult(dictionary.getType(typeClass), expectedResult);
    }

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef,
            SDictionary dictionary, Class<T> typeClass, String expectedResult) {
        assertAspectResult(aspectRef, dictionary.getType(typeClass), expectedResult);
    }

    static <T extends SType<?>> void assertAspectResult(T type, String expectedResult) {
        assertAspectResult(ASPECT_MY_INTERFACE, type, expectedResult);
    }

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef, T type,
            String expectedResult) {
        Optional<A> aspect = type.getAspect(aspectRef);
        String found = aspect.map(a -> a.get()).orElse(null);
        Assert.assertEquals(expectedResult, found);

        if (type.getInstanceClass() != null) {
            SInstance instance = type.newInstance();
            aspect = instance.getAspect(aspectRef);
            found = aspect.map(a -> a.get()).orElse(null);
            Assert.assertEquals(expectedResult, found);
        }
    }

    //---------------------------------------------------------
    //Aspect with a default registry and without a qualifier
    //---------------------------------------------------------

    public static final AspectRef<MyInterface> ASPECT_MY_INTERFACE = new AspectRef<>(MyInterface.class,
            MyInterfaceRegistry.class);

    @FunctionalInterface
    private static interface MyInterface extends Supplier<String> {
    }

    public static class MyInterfaceRegistry extends SingleAspectRegistry<MyInterface, Object> {

        public MyInterfaceRegistry(@Nonnull AspectRef<MyInterface> aspectRef) {
            super(aspectRef);
            addFixImplementation(STypeString.class, () -> "string");
            addFixImplementation(STypeSimple.class, () -> "simple");
        }
    }

    //---------------------------------------------------------
    //Aspect without default registry and qualifier
    //---------------------------------------------------------

    public static final AspectRef<MyInterface2> ASPECT_MY_INTERFACE2 = new AspectRef<>(MyInterface2.class);

    @FunctionalInterface
    private static interface MyInterface2 extends Supplier<String> {
    }

    //---------------------------------------------------------
    //Aspect with qualifier strategy
    //---------------------------------------------------------

    @Test
    public void testAspectWithDefaultRegisterAndQualifier() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pkg = dictionary.createNewPackage("test");

        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeString.class, "string0");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeEMail.class, "string5");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeComposite.class, "composite8");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeAttachment.class, "composite8");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeList.class, null);

        STypeString address = pkg.createType("address", STypeString.class);
        assertAspectResult(ASPECT_MY_INTERFACE3, address, "string0");
        address.asAtr().label("address");
        assertAspectResult(ASPECT_MY_INTERFACE3, address, "string5");

        dictionary.getType(STypeAttachment.class).setAspectFixImplementation(ASPECT_MY_INTERFACE3, () -> "attachment0");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeAttachment.class, "attachment0");
        assertAspectResult(ASPECT_MY_INTERFACE3, dictionary, STypeComposite.class, "composite8");

    }

    @FunctionalInterface
    private static interface MyInterface3 extends Supplier<String> {
    }

    public static final AspectRef<MyInterface3> ASPECT_MY_INTERFACE3 = new AspectRef<>(MyInterface3.class,
            MyInterfaceRegistry3.class);

    public static class MyInterfaceRegistry3 extends SingleAspectRegistry<MyInterface3, Integer> {

        public MyInterfaceRegistry3(@Nonnull AspectRef<MyInterface3> aspectRef) {
            super(aspectRef, new MyInterface3QualifierByLabelDistance());
            addFixImplementation(STypeComposite.class, 8, () -> "composite8");
            addFixImplementation(STypeString.class, null, () -> "string0");
            addFixImplementation(STypeString.class, 5, () -> "string5");
            addFixImplementation(STypeSimple.class, 10, () -> "simple10");
        }
    }

    public static final class MyInterface3QualifierByLabelDistance implements QualifierStrategy<Integer> {

        @Override
        public QualifierMatcher<Integer> getMatcherFor(SType<?> type) {
            return new MyInterface3QualifierMatcher(type.asAtr().getLabel());
        }

        private static class MyInterface3QualifierMatcher implements QualifierMatcher<Integer> {

            private final int targetLabelSize;

            private MyInterface3QualifierMatcher(String label) {
                this.targetLabelSize = label == null ? 0 : label.length();
            }

            @Override
            public boolean isMatch(@Nonnull AspectEntry<?, Integer> aspectEntry) {
                return true;
            }

            @Override
            public int compare(AspectEntry<?, Integer> o1, AspectEntry<?, Integer> o2) {
                return labelSizeDistance(o1) - labelSizeDistance(o2);
            }

            private int labelSizeDistance(AspectEntry<?, Integer> o1) {
                int qualifier = o1.getQualifier() == null ? 0 : o1.getQualifier();
                return Math.abs(targetLabelSize - qualifier);
            }
        }
    }
}
