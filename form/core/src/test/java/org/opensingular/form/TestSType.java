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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.TestSType.MyPackageA.MyTypeA;
import org.opensingular.form.TestSType.MyPackageA.MyTypeB;
import org.opensingular.form.TestSType.MyPackageA.MyTypeBB;
import org.opensingular.form.testPackage.ConflictPackageType;
import org.opensingular.form.testPackage.MyPackageYYYY;
import org.opensingular.form.testPackage.MyTypeWithInfoWithoutPackage;
import org.opensingular.form.testPackage.MyTypeWithoutInfo;
import org.opensingular.form.type.basic.AtrBasic;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.base.SingularException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Daniel C. Bordin on 24/03/2017.
 */
@RunWith(Parameterized.class)
public class TestSType extends TestCaseForm {

    public TestSType(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testIsTypeOf() {
        SDictionary dic1 = createTestDictionary();
        SDictionary dic2 = createTestDictionary();
        assertTrue(dic1.getType(STypeString.class).isTypeOf(dic1.getType(STypeString.class)));
        assertTrue(dic1.getType(STypeString.class).isTypeOf(dic1.getType(STypeSimple.class)));
        //noinspection unchecked
        assertFalse(dic1.getType(STypeSimple.class).isTypeOf(dic1.getType(STypeString.class)));

        assertThatThrownBy(() -> dic1.getType(STypeString.class).isTypeOf(dic2.getType(STypeString.class)))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "foi criado em outro dicionário");
    }

    @Test
    public void testConstructorWithExplicitName() {
        SType<SIString> type = new SType<>("meuTipo", SIString.class);
        Assert.assertEquals(type.getNameSimple(), "meuTipo");
    }

    @Test
    public void testConstructorWithImplicitName() {
        MyTypeA type = new MyTypeA();
        Assert.assertEquals(type.getNameSimple(), MyTypeA.class.getSimpleName());
    }

    @Test
    public void testConstructorWithAnnotatedName() {
        MyTypeB type = new MyTypeB();
        Assert.assertEquals(type.getNameSimple(), "TypeB");

        MyTypeBB typeBB = new MyTypeBB();
        Assert.assertEquals(typeBB.getNameSimple(), "TypeBB");
    }

    @SInfoPackage(name = "xx")
    public static class MyPackageA extends SPackage {
        @SInfoType(spackage = MyPackageA.class)
        public static class MyTypeA extends STypeDecimal {
        }

        @SInfoType(name = "TypeB", spackage = MyPackageA.class)
        public static class MyTypeB extends STypeDecimal {
        }

        @SInfoType(name = "TypeBB", spackage = MyPackageA.class)
        public static class MyTypeBB extends MyTypeB {
        }
    }

    @Test
    public void testExists() {
        STypeComposite<SIComposite> type = createSimpleComposite();
        assertTrue(type.exists());
        assertTrue(type.newInstance().exists());

        type = createSimpleComposite();
        type.asAtr().exists(Boolean.FALSE);
        assertFalse(type.exists());
        assertFalse(type.newInstance().exists());

        //Teste o caso dinâmico
        type = createSimpleComposite();
        type.getField("a").asAtr().exists(fieldA -> fieldA.getParent().getValue("b", Boolean.class));
        SIComposite block = type.newInstance();
        assertTrue(block.getField("a").exists());
        block.setValue("b", Boolean.FALSE);
        assertTrue(block.getField("a").exists());
        block.getDocument().updateAttributes(null); //Hoje é necessário o update para ver o resultado
        assertFalse(block.getField("a").exists());

        block.setValue("b", Boolean.TRUE);
        assertFalse(block.getField("a").exists());
        block.getDocument().updateAttributes(null); //Hoje é necessário o update para ver o resultado
        assertTrue(block.getField("a").exists());
    }

    private STypeComposite<SIComposite> createSimpleComposite() {
        STypeComposite<SIComposite> block = createTestPackage().createType("block", STypeComposite.class);
        block.addFieldString("a");
        block.addFieldBoolean("b").withDefaultValueIfNull(Boolean.TRUE);
        return block;
    }

    @Test
    public void as() {
        SType<SIString> type = new SType<>("meuTipo", SIString.class);

        type.as(AtrBasic.class);

        SingularTestUtil.assertException(() -> type.as(List.class), SingularFormException.class,
                "não funciona como aspecto");

        SingularTestUtil.assertException(() -> type.as(WrongClass.class), SingularException.class,
                "Fail to instantiate class");
    }

    public abstract class WrongClass extends STranslatorForAttribute {
    }

    @Test
    public void testPackageDefinitionIsOptional() {
        MyTypeWithInfoWithoutPackage t1 = createTestDictionary().getType(MyTypeWithInfoWithoutPackage.class);
        assertThat(t1.getPackage()).isNotNull();
        assertThat(t1.getPackage().getName()).isEqualTo(t1.getClass().getPackage().getName());

        MyTypeWithoutInfo t2 = t1.getDictionary().getType(MyTypeWithoutInfo.class);
        assertThat(t2.getPackage()).isSameAs(t1.getPackage());


        t2 = createTestDictionary().getType(MyTypeWithoutInfo.class);
        assertThat(t2.getPackage()).isNotNull();
        assertThat(t2.getPackage().getName()).isEqualTo(t2.getClass().getPackage().getName());
        t1 = t2.getDictionary().getType(MyTypeWithInfoWithoutPackage.class);
        assertThat(t2.getPackage()).isSameAs(t1.getPackage());
    }

    @Test
    public void testConflictPackage() {
        ConflictPackageType t1 = createTestDictionary().getType(ConflictPackageType.class);
        assertThat(t1.getPackage().getName()).isEqualTo(t1.getClass().getPackage().getName());

        assertThatThrownBy(() -> t1.getDictionary().loadPackage(MyPackageYYYY.class)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("como sendo do pacote 'yyyy'");

        assertThatThrownBy(() -> createTestDictionary().loadPackage(MyPackageYYYY.class)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("como sendo do pacote 'yyyy'");

    }

}
