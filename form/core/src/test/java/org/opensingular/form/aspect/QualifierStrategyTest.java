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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
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
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author Daniel C. Bordin on 16/08/2017.
 */
@RunWith(Parameterized.class)
public class QualifierStrategyTest extends TestCaseForm {

    public QualifierStrategyTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testAspectWithDefaultRegisterAndQualifier() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pkg = dictionary.createNewPackage("test");

        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeString.class, "string0");
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeEMail.class, "string0");
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeComposite.class, null);
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeAttachment.class, null);
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeList.class, null);
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeInteger.class, null);

        STypeString address = pkg.createType("address", STypeString.class);
        assertAspectResult(ASPECT_BY_EQUALS, address, "string0");
        address.asAtr().label("123456");
        assertAspectResult(ASPECT_BY_EQUALS, address, "string0");
        address.asAtr().label("12345");
        assertAspectResult(ASPECT_BY_EQUALS, address, "string5");
        address.asAtr().label("1234567890");
        assertAspectResult(ASPECT_BY_EQUALS, address, "simple10");

        STypeAttachment pdf1 = pkg.createType("pdf1", STypeAttachment.class);
        assertAspectResult(ASPECT_BY_EQUALS, pdf1, null);
        pdf1.asAtr().label("12345678");
        assertAspectResult(ASPECT_BY_EQUALS, pdf1, "composite8");

        dictionary.getType(STypeAttachment.class).setAspectFixImplementation(ASPECT_BY_EQUALS, () -> "attachment0");
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeAttachment.class, "attachment0");
        assertAspectResult(ASPECT_BY_EQUALS, dictionary, STypeComposite.class, null);

        STypeAttachment pdf2 = pkg.createType("pdf2", STypeAttachment.class);
        assertAspectResult(ASPECT_BY_EQUALS, pdf2, "attachment0");
        pdf2.asAtr().label("12345678");
        assertAspectResult(ASPECT_BY_EQUALS, pdf2, "attachment0");
    }

    @FunctionalInterface
    private static interface MyInterfaceByEquals extends Supplier<String> {
    }

    public static final AspectRef<MyInterfaceByEquals> ASPECT_BY_EQUALS = new AspectRef<>(MyInterfaceByEquals.class,
            MyRegistryByEquals.class);

    public static class MyRegistryByEquals extends SingleAspectRegistry<MyInterfaceByEquals, Integer> {

        public MyRegistryByEquals(@Nonnull AspectRef<MyInterfaceByEquals> aspectRef) {
            super(aspectRef, new QualifierStrategyByEquals<Integer>() {
                @Nullable
                @Override
                protected Integer extractQualifier(@Nonnull SType<?> type) {
                    return type.asAtr().getLabel() == null ? null : type.asAtr().getLabel().length();
                }
            });
            addFixImplementation(STypeComposite.class, 8, () -> "composite8");
            addFixImplementation(STypeString.class, null, () -> "string0");
            addFixImplementation(STypeString.class, 5, () -> "string5");
            addFixImplementation(STypeSimple.class, 10, () -> "simple10");
        }
    }

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef,
            SDictionary dictionary, Class<T> typeClass, String expectedResult) {
        AspectRefTest.assertAspectResult(aspectRef, dictionary, typeClass, expectedResult);
    }

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef, T type,
            String expectedResult) {
        AspectRefTest.assertAspectResult(aspectRef, type, expectedResult);
    }
}