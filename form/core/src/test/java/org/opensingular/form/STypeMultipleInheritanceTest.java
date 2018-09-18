package org.opensingular.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.MultiBaseA;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.MultiBaseB;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.MultiBaseC;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.MultiListM;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.MultiListN;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.RecursiveTypeA;
import org.opensingular.form.STypeMultipleInheritanceTest.PackageBase.RecursiveTypeB;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-10
 */
@RunWith(Parameterized.class)
public class STypeMultipleInheritanceTest extends TestCaseForm {

    public STypeMultipleInheritanceTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void multipleInheritanceOfCompositeByClass() {
        SDictionary dictionary = createTestDictionary();

        // C0 -> B0 -> A0
        MultiBaseA typeA0 = dictionary.getType(MultiBaseA.class);
        MultiBaseB typeB0 = dictionary.getType(MultiBaseB.class);
        MultiBaseC typeC0 = dictionary.getType(MultiBaseC.class);

        assertType(typeB0).isExtensionOf(typeA0).isExtensionCorrect(typeA0);
        assertType(typeC0).isExtensionOf(typeB0).isExtensionCorrect(typeB0).isExtensionOf(typeA0);

        SPackage pkg = typeA0.getPackage();

        // A1 -> A0
        MultiBaseA typeA1 = pkg.extendType("typeA1", MultiBaseA.class);
        assertType(typeA1).isExtensionOf(typeA0).isExtensionCorrect(typeA0);
        assertType(typeA1.fieldA3).isExtensionCorrect(typeA0.fieldA3);
        assertType(typeA1.fieldA3.fieldX1).isExtensionCorrect(typeA0.fieldA3.fieldX1);
        assertType(typeA0).isNotExtensionOf(typeA1).isComposite();

        // C1 -> (C0 + A1)
        MultiBaseC typeC1 = pkg.extendMultipleTypes("typeC1", MultiBaseC.class, typeA1);
        assertType(typeC1).isExtensionOf(typeC0).isExtensionCorrect(typeC0).isExtensionOf(typeB0).isExtensionOf(typeA1)
                .isExtensionOf(typeA0);
        assertType(typeC1).isComplementaryExtensionCorrect(typeA1);
        assertType(typeA0).isNotExtensionOf(typeC1);

        assertType(typeC1.fieldA1).isDirectExtensionOf(typeC0.fieldA1).isComplementaryExtensionCorrect(typeA1.fieldA1);
        assertType(typeC1.fieldB1).isDirectExtensionOf(typeC0.fieldB1).isDirectComplementaryExtensionOf(null);
        assertType(typeC1.fieldC1).isDirectExtensionOf(typeC0.fieldC1).isDirectComplementaryExtensionOf(null);
        assertType(typeC1.fieldA3).isExtensionCorrect(typeC0.fieldA3).isComplementaryExtensionCorrect(typeA1.fieldA3);
        assertType(typeC1.fieldA3.fieldX1).isExtensionCorrect(typeC0.fieldA3.fieldX1).isComplementaryExtensionCorrect(
                typeA1.fieldA3.fieldX1);

        MultiBaseC typeC1b = pkg.findOrCreateExtendedType(MultiBaseC.class, typeA1);
        assertType(typeC1b).isSameAs(typeC1);

        // B1 -> (B0 + A1)
        MultiBaseB typeB1 = pkg.extendMultipleTypes("typeB1", MultiBaseB.class, typeA1);
        assertType(typeB1).isExtensionOf(typeB0).isExtensionCorrect(typeB0);
        assertType(typeB1).isComplementaryExtensionCorrect(typeA1);

        assertType(typeC1).isNotExtensionOf(typeB1);

        MultiBaseB typeB1b = pkg.findOrCreateExtendedType(MultiBaseB.class, typeA1);
        assertType(typeB1b).isSameAs(typeB1);

        // C2 -> (C0 + A2)
        MultiBaseA typeA2 = pkg.extendType("typeA2", typeA1);
        MultiBaseC typeC2 = pkg.extendMultipleTypes("typeC2", MultiBaseC.class, typeA2);
        assertType(typeC2).isExtensionOf(typeC0).isExtensionCorrect(typeC0).isExtensionOf(typeB0).isExtensionOf(typeA2)
                .isExtensionOf(typeA1).isExtensionOf(typeA0);
        assertType(typeC2).isNotExtensionOf(typeC1);
        assertType(typeC2).isComplementaryExtensionCorrect(typeA2);

        assertType(typeC2.fieldA1).isDirectExtensionOf(typeC0.fieldA1).isComplementaryExtensionCorrect(typeA2.fieldA1);
        assertType(typeC2.fieldB1).isDirectExtensionOf(typeC0.fieldB1).isDirectComplementaryExtensionOf(null);
        assertType(typeC2.fieldC1).isDirectExtensionOf(typeC0.fieldC1).isDirectComplementaryExtensionOf(null);
        assertType(typeC2.fieldA3).isExtensionCorrect(typeC0.fieldA3).isComplementaryExtensionCorrect(typeA2.fieldA3);
        assertType(typeC2.fieldA3.fieldX1).isExtensionCorrect(typeC0.fieldA3.fieldX1).isComplementaryExtensionCorrect(
                typeA2.fieldA3.fieldX1);

        MultiBaseC typeC2b = pkg.findOrCreateExtendedType(MultiBaseC.class, typeA2);
        assertType(typeC2b).isSameAs(typeC2);
    }

    @Test
    public void multipleInheritanceOfListByClass() {
        SDictionary dictionary = createTestDictionary();

        // C0 -> B0 -> A0
        MultiListM typeM0 = dictionary.getType(MultiListM.class);
        MultiListN typeN0 = dictionary.getType(MultiListN.class);
        SPackage pkg = typeN0.getPackage();

        assertType(typeN0).isExtensionOf(typeM0).isExtensionCorrect(typeM0);

        // M1 -> M0
        MultiListM typeM1 = pkg.extendType("typeM1", MultiListM.class);
        assertType(typeM1).isExtensionOf(typeM0).isExtensionCorrect(typeM0);

        assertType(typeM1.listA1).isExtensionCorrect(typeM0.listA1);
        assertType(typeM1.listA1.getElementsType()).isExtensionCorrect(typeM0.listA1.getElementsType());
        assertType(typeM1.listA1.getElementsType().fieldA1).isExtensionCorrect(typeM0.listA1.getElementsType().fieldA1);
        assertType(typeM1.listC1).isExtensionCorrect(typeM0.listC1);
        assertType(typeM1.listC1.getElementsType()).isExtensionCorrect(typeM0.listC1.getElementsType());

        //  N1 -> (N0 + M1)
        MultiListN typeN1 = pkg.extendMultipleTypes("typeN1", MultiListN.class, typeM1);
        assertType(typeN1).isExtensionOf(typeN0).isExtensionCorrect(typeN0).isExtensionOf(typeM0).isExtensionOf(typeM1);

        assertType(typeN1.listA1).isExtensionCorrect(typeN0.listA1);
        assertType(typeN1.listA1.getElementsType()).isExtensionCorrect(typeN0.listA1.getElementsType());
        assertType(typeN1.listA1.getElementsType().fieldA1).isExtensionCorrect(typeN0.listA1.getElementsType().fieldA1);
        assertType(typeN1.listC1).isExtensionCorrect(typeN0.listC1);
        assertType(typeN1.listC1.getElementsType()).isExtensionCorrect(typeN0.listC1.getElementsType());
        assertType(typeN1.listB1).isExtensionCorrect(typeN0.listB1);
        assertType(typeN1.listB1.getElementsType()).isExtensionCorrect(typeN0.listB1.getElementsType());

        assertType(typeN1.listA1).isDirectComplementaryExtensionOf(typeM1.listA1);
        assertType(typeN1.listA1.getElementsType()).isDirectComplementaryExtensionOf(typeM1.listA1.getElementsType());
        assertType(typeN1.listA1.getElementsType().fieldA1).isDirectComplementaryExtensionOf(
                typeM1.listA1.getElementsType().fieldA1);
        assertType(typeN1.listC1).isDirectComplementaryExtensionOf(typeM1.listC1);
        assertType(typeN1.listC1.getElementsType()).isDirectComplementaryExtensionOf(typeM1.listC1.getElementsType());
        assertType(typeN1).isComplementaryExtensionCorrect(typeM1);
    }

    @Test
    public void multipleInheritanceWithWrongReferences() {
        SDictionary dictionary = createTestDictionary();

        MultiBaseC typeC0 = dictionary.getType(MultiBaseC.class);
        SPackage pkg = typeC0.getPackage();

        MultiBaseB typeB1 = pkg.extendType("typeB1", MultiBaseB.class);

        SingularTestUtil.assertException(() -> pkg.extendMultipleTypes("typeB2", typeB1, typeC0),
                SingularFormException.class, "java class isn't a derived class of the type");
    }

    @Test
    public void mixingDictionaries() {
        MultiBaseA typeA0 = createTestDictionary().getType(MultiBaseA.class);
        SPackage pkg = typeA0.getPackage();

        assertThatThrownBy(() -> pkg.extendType("typeA1", createInNewDictionary(MultiBaseA.class))).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("foi criado em outro dicionário");

        assertThatThrownBy(
                () -> pkg.extendMultipleTypes("typeC1", MultiBaseC.class, createInNewDictionary(MultiBaseA.class)))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "foi criado em outro dicionário");

        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeC1", createInNewDictionary(MultiBaseC.class), typeA0))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "foi criado em outro dicionário");
    }

    @Test
    public void unnecessaryMultipleInheritance() {
        SDictionary dictionary = createTestDictionary();
        MultiBaseA typeA0 = dictionary.getType(MultiBaseA.class);
        MultiBaseB typeB0 = dictionary.getType(MultiBaseB.class);
        MultiBaseC typeC0 = dictionary.getType(MultiBaseC.class);
        SPackage pkg = typeA0.getPackage();

        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeA1", typeB0, typeA0)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("is already a direct super type of");
        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeA1", typeC0, typeA0)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("is already a direct super type of");


        MultiBaseA typeA1 = pkg.extendType("typeA1", typeA0);

        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeA2", typeA0, typeA1)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("is already a direct super type of");

        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeA2", typeA1, typeA0)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("is already a direct super type of");
    }


    @Test
    public void complementaryCompositeTypeCantHaveExtraFields() {
        SDictionary dictionary = createTestDictionary();

        // B0 -> A0
        MultiBaseA typeA0 = dictionary.getType(MultiBaseA.class);
        MultiBaseB typeB0 = dictionary.getType(MultiBaseB.class);

        // A1 -> A0
        SPackage pkg = typeA0.getPackage();
        MultiBaseA typeA1 = pkg.extendType("typeA1", MultiBaseA.class);
        typeA1.addFieldString("newFieldY");

        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeB1", typeB0, typeA1)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining(" has created a new field").hasMessageContaining(
                "newFieldY");

        MultiBaseA typeA2 = pkg.extendType("typeA2", MultiBaseA.class);
        typeA2.fieldA3.addFieldString("newFieldX");
        assertThatThrownBy(() -> pkg.extendMultipleTypes("typeB1", typeB0, typeA2)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining(" has created a new field").hasMessageContaining(
                "newFieldX");
    }


    @Test
    public void testRecursiveReferenceScenario() {
        SDictionary dictionary = createTestDictionary();
        // B0 -> A0
        RecursiveTypeA typeA0 = dictionary.getType(RecursiveTypeA.class);
        RecursiveTypeB typeB0 = dictionary.getType(RecursiveTypeB.class);
        SPackage pkg = typeB0.getPackage();


        // A1 -> A0
        RecursiveTypeA typeA1 = pkg.extendType("typeA1", RecursiveTypeA.class);
        assertType(typeA1).isExtensionOf(typeA0).isExtensionCorrect(typeA0);

        assertType(typeA1.field1).isExtensionCorrect(typeA0.field1);
        assertType(typeA1.field1.getElementsType()).isExtensionCorrect(typeA0.field1.getElementsType())
                .isRecursiveReference();
        assertType(typeA1.field1.getElementsType().field1).isSameAs(typeA0.field1.getElementsType().field1);
        assertType(typeA1.field2).isExtensionCorrect(typeA0.field2);
        assertType(typeA1.field3).isExtensionCorrect(typeA0.field3);

        //  B1 -> (B0 + A1)
        RecursiveTypeB typeB1 = pkg.extendMultipleTypes("typeB1", RecursiveTypeB.class, typeA1);

        assertType(typeB1.field1).isDirectExtensionOf(typeB0.field1).isDirectComplementaryExtensionOf(typeA1.field1);
        assertType(typeB1.field1.getElementsType()).isDirectExtensionOf(typeB0.field1.getElementsType())
                .isDirectComplementaryExtensionOf(typeA1.field1.getElementsType()).isRecursiveReference();
        assertType(typeB1.field1.getElementsType().field1).isSameAs(typeA0.field1.getElementsType().field1);
        assertType(typeB1.field1.getElementsType().field2).isSameAs(typeA0.field1.getElementsType().field2);

        assertType(typeB1.field1.getElementsType()).isComplementaryExtensionCorrect(typeA1.field1.getElementsType());
        assertType(typeB1).isComplementaryExtensionCorrect(typeA1).isExtensionOf(typeB0).isExtensionCorrect(typeB0)
                .isExtensionOf(typeA0).isExtensionOf(typeA1);

        typeA0.debug();
        typeA1.debug();
        typeB0.debug();
        typeB1.debug();

        SIComposite b1 = typeB1.newInstance();
        b1.setValue(typeA0.field3, 1);

        SIComposite a11 = b1.getField(typeA0.field1).addNew();
        a11.setValue(typeA0.field3, 11);

        SIComposite a111 = a11.getField(typeA0.field1).addNew();
        a111.setValue(typeA0.field3, 111);

        assertInstance(b1.getField(typeA0.field1)).isExactTypeOf(typeB1.field1);
        assertInstance(b1).field("field1[0]").isExactTypeOf(typeB1.field1.getElementsType());
        assertInstance(b1).field("field1[0].field3").isValueEquals(11);
        assertInstance(b1).field("field1[0].field1").isExactTypeOf(typeA0.field1);
        assertInstance(b1).field("field1[0].field1[0]").isExactTypeOf(typeA0.field1.getElementsType());
        assertInstance(b1).field("field1[0].field1[0].field3").isValueEquals(111);
        assertInstance(b1).assertCorrectStructure();
    }

    @Test
    public void testAttributeInheritance() {
        testAttributeInheritance(t -> t);
        testAttributeInheritance(t -> t.fieldA1);
        testAttributeInheritance(t -> t.fieldA3);
        testAttributeInheritance(t -> t.fieldA3.fieldX1);
    }

    private void testAttributeInheritance(@Nonnull  Function<MultiBaseA, SType<?>> fieldFinder) {
        SDictionary dictionary = createTestDictionary();
        // C0 -> B0 -> A0
        MultiBaseA typeA0 = dictionary.getType(MultiBaseA.class);
        MultiBaseB typeB0 = dictionary.getType(MultiBaseB.class);
        MultiBaseC typeC0 = dictionary.getType(MultiBaseC.class);
        SPackage pkg = typeA0.getPackage();
        // A1 -> A0; A2 -> A1
        MultiBaseA typeA1 = pkg.extendType("typeA1", MultiBaseA.class);
        MultiBaseA typeA2 = pkg.extendType("typeA2", typeA1);
        // C1 -> (C0 + A1)
        MultiBaseC typeC1 = pkg.extendMultipleTypes("typeC1", MultiBaseC.class, typeA1);
        // B1 -> (B0 + A1)
        MultiBaseB typeB1 = pkg.extendMultipleTypes("typeB1", MultiBaseB.class, typeA1);
        // C2 -> (C0 + A2)
        MultiBaseC typeC2 = pkg.extendMultipleTypes("typeC2", MultiBaseC.class, typeA2);

        SType<?> fieldA0 = fieldFinder.apply(typeA0);
        SType<?> fieldA1 = fieldFinder.apply(typeA1);
        SType<?> fieldB0 = fieldFinder.apply(typeB0);
        SType<?> fieldB1 = fieldFinder.apply(typeB1);
        SType<?> fieldC0 = fieldFinder.apply(typeC0);
        SType<?> fieldC1 = fieldFinder.apply(typeC1);
        SType<?> fieldC2 = fieldFinder.apply(typeC2);

        fieldA0.asAtr().label("A0");
        fieldB0.asAtr().label("B0");
        fieldC0.asAtr().label("C0");

        assertType(fieldA0).isAttrLabel("A0"); //A0
        assertType(fieldB0).isAttrLabel("B0"); //B0 (<- A0)
        assertType(fieldC0).isAttrLabel("C0"); //C0 (<- B0)
        assertType(fieldA1).isAttrLabel("A0"); //   (<- A0)
        assertType(fieldB1).isAttrLabel("B0"); //   (<- B0 + A1)
        assertType(fieldC1).isAttrLabel("C0"); //   (<- C0 + A1)
        assertType(fieldC2).isAttrLabel("C0"); //   (<- C0 + A2)

        fieldA1.asAtr().label("A1");

        assertType(fieldA0).isAttrLabel("A0"); //A0
        assertType(fieldB0).isAttrLabel("B0"); //B0 (<- A0)
        assertType(fieldC0).isAttrLabel("C0"); //C0 (<- B0)
        assertType(fieldA1).isAttrLabel("A1"); //A1 (<- A0)
        assertType(fieldB1).isAttrLabel("A1"); //   (<- B0 + A1)
        assertType(fieldC1).isAttrLabel("A1"); //   (<- C0 + A1)
        assertType(fieldC2).isAttrLabel("A1"); //   (<- C0 + A2)

        fieldC1.asAtr().label("C1");

        assertType(fieldA0).isAttrLabel("A0"); //A0
        assertType(fieldB0).isAttrLabel("B0"); //B0 (<- A0)
        assertType(fieldC0).isAttrLabel("C0"); //C0 (<- B0)
        assertType(fieldA1).isAttrLabel("A1"); //A1 (<- A0)
        assertType(fieldB1).isAttrLabel("A1"); //   (<- B0 + A1)
        assertType(fieldC1).isAttrLabel("C1"); //C1 (<- C0 + A1)
        assertType(fieldC2).isAttrLabel("A1"); //   (<- C0 + A2)

        fieldA0.asAtr().subtitle("A0");
        fieldA1.asAtr().subtitle("A1");
        fieldB0.asAtr().subtitle("B0");
        fieldB1.asAtr().subtitle("B1");
        fieldC0.asAtr().subtitle("C0");

        assertType(fieldA0).isAttrSubTitle("A0"); //A0
        assertType(fieldB0).isAttrSubTitle("B0"); //B0 (<- A0)
        assertType(fieldC0).isAttrSubTitle("C0"); //C0 (<- B0)
        assertType(fieldA1).isAttrSubTitle("A1"); //A1 (<- A0)
        assertType(fieldB1).isAttrSubTitle("B1"); //B1 (<- B0 + A1)
        assertType(fieldC1).isAttrSubTitle("A1"); //   (<- C0 + A1)
        assertType(fieldC2).isAttrSubTitle("A1"); //   (<- C0 + A2)
    }

    @Nonnull
    private final <T extends SType<?>> T createInNewDictionary(@Nonnull Class<T> type) {
        return createTestDictionary().getType(type);
    }

    @SInfoPackage
    static class PackageBase extends SPackage {

        @SInfoType(spackage = PackageBase.class)
        public static class SubBlockX extends STypeComposite<SIComposite> {
            public STypeInteger fieldX1;
            public STypeString fieldX2;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldX1 = addFieldInteger("fieldX1");
                fieldX2 = addFieldString("fieldX2");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class MultiListM extends STypeComposite<SIComposite> {
            public STypeList<MultiBaseA, SIComposite> listA1;
            public STypeList<MultiBaseC, SIComposite> listC1;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                listA1 = addFieldListOf("listA1", MultiBaseA.class);
                listC1 = addFieldListOf("listC1", MultiBaseC.class);
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class MultiListN extends MultiListM {
            public STypeList<MultiBaseB, SIComposite> listB1;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                listB1 = addFieldListOf("listB1", MultiBaseB.class);
            }
        }


        @SInfoType(spackage = PackageBase.class)
        public static class MultiBaseA extends STypeComposite<SIComposite> {
            public STypeInteger fieldA1;
            public STypeString fieldA2;
            public SubBlockX fieldA3;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldA1 = addFieldInteger("fieldA1");
                fieldA2 = addFieldString("fieldA2");
                fieldA3 = addField("fieldA3", SubBlockX.class);
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class MultiBaseB extends MultiBaseA {
            public STypeString fieldB1;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldB1 = addFieldString("fieldB1");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class MultiBaseC extends MultiBaseB {
            public STypeString fieldC1;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldC1 = addFieldString("fieldC1");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class RecursiveTypeA extends STypeComposite<SIComposite> {
            public STypeList<RecursiveTypeA, SIComposite> field1;
            public SubBlockX field2;
            public STypeInteger field3;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                field1 = addFieldListOf("field1", RecursiveTypeA.class);
                field2 = addField("field2", SubBlockX.class);
                field3 = addFieldInteger("field3");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class RecursiveTypeB extends RecursiveTypeA {
            public STypeInteger field4;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                field4 = addFieldInteger("field4");
            }
        }
    }
}
