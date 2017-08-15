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
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.util.STypeEMail;

import javax.annotation.Nonnull;
import java.util.Optional;

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
        Assert.assertEquals("string", aspect.orElse(null).getText());

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

    private <T extends SType<?>> void assertAspectResult(SDictionary dictionary, Class<T> typeClass,
            String expectedResult) {
        assertAspectResult(dictionary.getType(typeClass), expectedResult);
    }

    private <A extends MyInterface, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef,
            SDictionary dictionary, Class<T> typeClass, String expectedResult) {
        assertAspectResult(aspectRef, dictionary.getType(typeClass), expectedResult);
    }

    private <T extends SType<?>> void assertAspectResult(T type, String expectedResult) {
        assertAspectResult(ASPECT_MY_INTERFACE, type, expectedResult);
    }

    private <A extends MyInterface, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef, T type,
            String expectedResult) {
        Optional<A> aspect = type.getAspect(aspectRef);
        String found = aspect.map(a -> a.getText()).orElse(null);
        Assert.assertEquals(expectedResult, found);

        if (type.getInstanceClass() != null) {
            SInstance instance = type.newInstance();
            aspect = instance.getAspect(aspectRef);
            found = aspect.map(a -> a.getText()).orElse(null);
            Assert.assertEquals(expectedResult, found);
        }
    }

    public static final AspectRef<MyInterface> ASPECT_MY_INTERFACE = new AspectRef<>(MyInterface.class,
            MyInterfaceRegistry.class);

    @FunctionalInterface
    private static interface MyInterface {
        public String getText();
    }

    public static class MyInterfaceRegistry extends SingleAspectRegistry<MyInterface, Object> {

        public MyInterfaceRegistry(@Nonnull AspectRef<MyInterface> aspectRef) {
            super(aspectRef);
            addFixImplementation(STypeString.class, () -> "string");
            addFixImplementation(STypeSimple.class, () -> "simple");
        }
    }

    public static final AspectRef<MyInterface2> ASPECT_MY_INTERFACE2 = new AspectRef<>(MyInterface2.class);

    @FunctionalInterface
    private static interface MyInterface2 extends MyInterface {
        public String getText();
    }

}
