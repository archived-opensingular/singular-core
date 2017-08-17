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
    public void testAspectWithDefaultRegisterAndQualifierByEquals() {
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

    @Test
    public void testAspectWithQualifierByClass() {
        SDictionary dictionary = createTestDictionary();
        PackageBuilder pkg = dictionary.createNewPackage("test");

        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeString.class, "stringNull");
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeEMail.class, "stringNull");
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeComposite.class, null);
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeAttachment.class, null);
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeList.class, null);
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeInteger.class, "simpleNull");

        STypeString address = pkg.createType("address", STypeString.class);
        address.asAtr().label("a");
        assertAspectResult(ASPECT_BY_CLASS, address, "stringNull");
        address.asAtr().label("aa");
        assertAspectResult(ASPECT_BY_CLASS, address, "stringAA");
        address.asAtr().label("aaa");
        assertAspectResult(ASPECT_BY_CLASS, address, "simpleAAA");

        STypeEMail mail = pkg.createType("mail", STypeEMail.class);
        mail.asAtr().label("a");
        assertAspectResult(ASPECT_BY_CLASS, mail, "stringNull");
        mail.asAtr().label("aa");
        assertAspectResult(ASPECT_BY_CLASS, mail, "stringAA");
        mail.asAtr().label("aaa");
        assertAspectResult(ASPECT_BY_CLASS, mail, "simpleAAA");

        STypeInteger qtd = pkg.createType("qtd", STypeInteger.class);
        qtd.asAtr().label("a");
        assertAspectResult(ASPECT_BY_CLASS, qtd, "simpleNull");
        qtd.asAtr().label("aa");
        assertAspectResult(ASPECT_BY_CLASS, qtd, "simpleNull");
        qtd.asAtr().label("aaa");
        assertAspectResult(ASPECT_BY_CLASS, qtd, "simpleAAA");


        STypeAttachment pdf1 = pkg.createType("pdf1", STypeAttachment.class);
        pdf1.asAtr().label("a");
        assertAspectResult(ASPECT_BY_CLASS, pdf1, null);
        pdf1.asAtr().label("aa");
        assertAspectResult(ASPECT_BY_CLASS, pdf1, "compositeAA");
        pdf1.asAtr().label("aaa");
        assertAspectResult(ASPECT_BY_CLASS, pdf1, "compositeAA");

        dictionary.getType(STypeAttachment.class).setAspectFixImplementation(ASPECT_BY_CLASS, () -> "attachment0");
        assertAspectResult(ASPECT_BY_CLASS, dictionary, STypeAttachment.class, "attachment0");
        assertAspectResult(ASPECT_BY_CLASS, pdf1, "attachment0");

    }

    @FunctionalInterface
    private static interface MyInterfaceByClass extends Supplier<String> {
    }

    public static final AspectRef<MyInterfaceByClass> ASPECT_BY_CLASS = new AspectRef<>(MyInterfaceByClass.class,
            MyRegistryByClass.class);

    public static class MyRegistryByClass extends SingleAspectRegistry<MyInterfaceByClass, Class<? extends Q_A>> {

        public MyRegistryByClass(@Nonnull AspectRef<MyInterfaceByClass> aspectRef) {
            super(aspectRef, new QualifierStrategyMyRegistry());
            addFixImplementation(STypeComposite.class, Q_AA.class, () -> "compositeAA");
            addFixImplementation(STypeString.class, null, () -> "stringNull");
            addFixImplementation(STypeString.class, Q_AA.class, () -> "stringAA");
            addFixImplementation(STypeSimple.class, null, () -> "simpleNull");
            addFixImplementation(STypeSimple.class, Q_AAA.class, () -> "simpleAAA");
        }

        public static class QualifierStrategyMyRegistry extends QualifierStrategyByClassQualifier<Class<? extends Q_A>> {
            @Nullable
            @Override
            protected Class<? extends Q_A> extractQualifier(@Nonnull SType<?> type) {
                String label = type.asAtr().getLabel();
                if ("a".equals(label)) {
                    return Q_A.class;
                } else if ("aa".equals(label)) {
                    return Q_AA.class;
                } else if ("aaa".equals(label)) {
                    return Q_AAA.class;
                }
                return null;
            }
        }
    }

    public static class Q_A {
    }

    public static class Q_AA extends Q_A {
    }

    public static class Q_AAA extends Q_AA {
    }

    // -------------------------- Helpers for the tests

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef,
            SDictionary dictionary, Class<T> typeClass, String expectedResult) {
        AspectRefTest.assertAspectResult(aspectRef, dictionary, typeClass, expectedResult);
    }

    static <A extends Supplier<String>, T extends SType<?>> void assertAspectResult(AspectRef<A> aspectRef, T type,
            String expectedResult) {
        AspectRefTest.assertAspectResult(aspectRef, type, expectedResult);
    }
}