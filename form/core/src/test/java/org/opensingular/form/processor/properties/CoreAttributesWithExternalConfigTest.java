package org.opensingular.form.processor.properties;
///*
// * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.opensingular.form;
//
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//import org.opensingular.form.type.core.SIInteger;
//import org.opensingular.form.type.core.SIString;
//import org.opensingular.form.type.core.STypeInteger;
//import org.opensingular.form.type.core.STypeString;
//
///**
// * @author Daniel C. Bordin on 28/04/2017.
// */
//@RunWith(Parameterized.class)
//public class CoreAttributesWithExternalConfigTest extends TestCaseForm {
//
//    public CoreAttributesWithExternalConfigTest(TestFormConfig testFormConfig) {
//        super(testFormConfig);
//    }

//    @Test
//    public void loadFromExternalFile() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttribute1.class));
//        type.isAttribute(SPackageBasic.ATR_LABEL, "LL1");
//        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "S1");
//        type.isAttribute(SPackageBasic.ATR_MAX_LENGTH, 100);
//    }
//
//    public static class PackageExternalAttr extends SPackage {
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttribute1 extends STypeString {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            setAttributeValue(SPackageBasic.ATR_LABEL, "L1");
//            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "S1");
//        }
//    }
//
//    @Test
//    public void loadFormExternalFileSecondLevel() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttribute2.class));
//        type.isAttribute(SPackageBasic.ATR_LABEL, "LL2");
//        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SS2");
//        type.isAttribute(SPackageBasic.ATR_MAX_LENGTH, 30);
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttribute2 extends STypeExternalAttribute1 {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            setAttributeValue(SPackageBasic.ATR_LABEL, "L2");
//            setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, 20);
//        }
//    }
//
//    @Test
//    public void loadFormExternal_setNull() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeNull.class));
//        type.isAttribute(SPackageBasic.ATR_LABEL, null);
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeNull extends STypeString {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            setAttributeValue(SPackageBasic.ATR_LABEL, "L2");
//        }
//    }
//
//    @Test
//    public void loadFormExternalLazy_withBadPropertiesFiles() {
//        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong1.class),
//                SingularFormException.class, "key='singular.form.basic.label'");
//
//        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong2.class),
//                SingularFormException.class, "key='bla@'");
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeWrong1 extends STypeString {
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeWrong2 extends STypeString {
//    }
//
//    @Test
//    public void loadFromExternalFileForComposite() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeComposite.class));
//        type.isAttribute(SPackageBasic.ATR_LABEL, "LLL1");
//        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS1");
//        type.field("field1").isAttribute(SPackageBasic.ATR_LABEL, "LLL2");
//        type.field("field1").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS2");
//        type.field("field2").isAttribute(SPackageBasic.ATR_LABEL, "LLL3");
//        type.field("field2").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS3");
//    }
//
//    @Test
//    @Ignore
//    public void performance() {
//        createTestDictionary().getType(STypeExternalAttributeComposite.class); //Para fazer caches
//        performance("String    ", 10000, this::simpleCall);
//        performance("Composite ", 10000, this::compositeCall);
//        performance("String2   ", 20000, this::simpleCall);
//        performance("Composite2", 20000, this::compositeCall);
//        performance("String3   ", 20000, this::simpleCall);
//        performance("Composite3", 20000, this::compositeCall);
//    }
//
//    private void simpleCall() {
//        STypeString type = createTestDictionary().getType(STypeString.class);
//        readAttributes(type);
//    }
//
//    private void readAttributes(SType<?> type) {
//        type.getAttributeValue(SPackageBasic.ATR_LABEL);
//        type.getAttributeValue(SPackageBasic.ATR_SUBTITLE);
//        type.getAttributeValue(SPackageBasic.ATR_REQUIRED);
//        type.getAttributeValue(SPackageBasic.ATR_VISIBLE);
//    }
//
//    private void compositeCall() {
//        STypeExternalAttributeComposite type = createTestDictionary().getType(STypeExternalAttributeComposite.class);
//        readAttributes(type);
//        readAttributes(type.getField("field1"));
//        readAttributes(type.getField("field2"));
//    }
//
//    private void performance(String name, int repeticoes, Runnable task) {
//        long tempo = System.currentTimeMillis();
//        for(int i = 0; i <repeticoes; i++) {
//            task.run();
//        }
//        tempo = System.currentTimeMillis() - tempo;
//        System.out.println("-------------------------------------------");
//        System.out.println("  " + name + ": T=" + SingularIOUtils.humanReadableMiliSeconds(tempo) + " R=" + repeticoes +
//                "  qtd/seg=" + ConversorToolkit.printNumber(1000.0 * repeticoes / tempo, 0));
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeComposite extends STypeComposite {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC11");
//            addFieldString("field1").setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC21");
//            addField("field2", STypeExternalAttribute1.class);
//        }
//    }
//
//    @Test
//    public void loadFromExternalFileForCompositeWrong() {
//        SingularTestUtil.assertException(
//                () -> createTestDictionary().getType(STypeExternalAttributeCompositeWrong.class),
//                SingularFormException.class, "Não foi encontrado o tipo 'field1000'");
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeCompositeWrong extends STypeComposite {
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            addFieldString("field1");
//        }
//    }
//
//    @Test
//    public void loadFormExternalLazy_withClassReadTriger() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeLazyLoad.class));
//
//        AtrRef<STypeString, SIString, String> atr1 = PackageDinamicAttr.ATR_TEXT1;
//        AtrRef<STypeInteger, SIInteger, Integer> atr2 = PackageDinamicAttr.ATR_INT1;
//        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//
//        type.isAttribute(SPackageBasic.ATR_LABEL, "LBL");
//        type.isAttribute(atr1.getNameFull(), "correct");
//        type.isAttribute(atr2.getNameFull(), "20");
//
//        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//
//        type.isAttribute(atr1, "correct");
//        type.isAttribute(atr2, 20);
//
//        //Depois das linhas a cima, então têm que ter convertido os valores
//        type.isAttribute(atr2.getNameFull(), 20);
//        assertEquals(SIInteger.class, type.getTarget().getAttributeDirectly(atr2.getNameFull()).get().getClass());
//
//        assertTrue(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertTrue(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//    }
//
//    @Test
//    public void loadFormExternalLazy_withInstanceReadTriger() {
//        SDictionary dictionary = createTestDictionary();
//        AssertionsSInstance instance = assertInstance(dictionary.newInstance(STypeExternalAttributeLazyLoad.class));
//
//        AtrRef<STypeString, SIString, String> atr1 = PackageDinamicAttr.ATR_TEXT1;
//        AtrRef<STypeInteger, SIInteger, Integer> atr2 = PackageDinamicAttr.ATR_INT1;
//        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//
//        instance.isAttribute(SPackageBasic.ATR_LABEL, "LBL");
//        instance.isAttribute(atr1.getNameFull(), "correct");
//        instance.isAttribute(atr2.getNameFull(), "20");
//
//        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//
//        instance.isAttribute(atr1, "correct");
//        instance.isAttribute(atr2, 20);
//
//        //Depois das linhas a cima, então têm que ter convertido os valores
//        instance.isAttribute(atr2.getNameFull(), 20);
//        assertEquals(SIInteger.class, instance.getTarget().getType().getAttributeDirectly(atr2.getNameFull()).get().getClass());
//
//        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
//        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
//    }
//
//    @SInfoType(spackage = PackageExternalAttr.class)
//    public static class STypeExternalAttributeLazyLoad extends STypeString {
//    }
//
//    @SInfoPackage(name = "dinamic")
//    public static class PackageDinamicAttr extends SPackage {
//
//        public static final AtrRef<STypeString, SIString, String> ATR_TEXT1 = new AtrRef<>(PackageDinamicAttr.class,
//                "text1", STypeString.class, SIString.class, String.class);
//
//        public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_INT1 = new AtrRef<>(PackageDinamicAttr.class,
//                "int1", STypeInteger.class, SIInteger.class, Integer.class);
//
//        protected void onLoadPackage(PackageBuilder pb) {
//            pb.createAttributeIntoType(SType.class, ATR_TEXT1);
//            pb.createAttributeIntoType(SType.class, ATR_INT1);
//        }
//    }
//
//}
