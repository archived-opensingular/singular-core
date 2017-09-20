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

package org.opensingular.form.processor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AtrRef;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;


@RunWith(Parameterized.class)
public class CoreXMLAttributesImporterTest extends TestCaseForm {

    public CoreXMLAttributesImporterTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void loadFromExternalFile() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttribute1.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, "LL1");
        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "S1");
        type.isAttribute(SPackageBasic.ATR_MAX_LENGTH, 100);
    }

    public static class PackageExternalAttr extends SPackage {
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttribute1 extends STypeString {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_LABEL, "L1");
            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "S1");
        }
    }

    @Test
    public void loadFormExternalFileSecondLevel() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttribute2.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, "LL2");
        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SS2");
        type.isAttribute(SPackageBasic.ATR_MAX_LENGTH, 30);
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttribute2 extends STypeExternalAttribute1 {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_LABEL, "L2");
            setAttributeValue(SPackageBasic.ATR_MAX_LENGTH, 20);
        }
    }

    @Test
    public void loadFormExternal_setNull() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeEmpty.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, null);
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeEmpty extends STypeString {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_LABEL, "L2");
        }
    }

    @Test
    public void loadFormExternal_wrong() {
        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong1.class),
              SingularFormException.class, null);
        //TODO verificar com daniel se que bloquear para atributos nao conhecidos
//        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong2.class),
//                SingularFormException.class, null);
        
        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong3.class),
                SingularFormException.class, null);
        
        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong4.class),
                SingularFormException.class, null);
    }
    
    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong1 extends STypeString {
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong2 extends STypeString {
    }
    
    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong3 extends STypeString {
    }
    
    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong4 extends STypeString {
    }
    
    

    @Test
    public void loadFromExternalFileForComposite() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeComposite2.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, "LLL1");
        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS1");
        type.field("field1").isAttribute(SPackageBasic.ATR_LABEL, "LLL2");
        type.field("field1").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS2");
        type.field("field2").isAttribute(SPackageBasic.ATR_LABEL, "LLL3");
        type.field("field2").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS3");
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeComposite2 extends STypeComposite {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC11");
            addFieldString("field1").setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC21");
            addField("field2", STypeExternalAttribute1.class);
        }
    }

    @Test
    public void loadFromExternalFileForCompositeComplex() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeCompositeComplex.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, "LLL1");
        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS1");
        type.field("field1").isAttribute(SPackageBasic.ATR_LABEL, "LLL2");
        type.field("field1").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS2");
        type.field("field2").isAttribute(SPackageBasic.ATR_LABEL, "LLL3");
        type.field("field2").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS3");
        type.field("field3").field("field1").isAttribute(SPackageBasic.ATR_LABEL, "LLL4");
        type.field("field3").field("field2").isAttribute(SPackageBasic.ATR_LABEL, "LLL5");
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeCompositeComplex extends STypeComposite {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC11");
            addFieldString("field1").setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC21");
            addField("field2", STypeExternalAttribute1.class);
            addField("field3", STypeExternalAttributeComp.class);
        }
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeComp extends STypeComposite {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            addFieldString("field1");
            addFieldString("field2");
        }
    }

    @Test
    @Ignore
    public void performance() {
        createTestDictionary().getType(STypeExternalAttributeComposite2.class); //Para fazer caches
        SingularTestUtil.performance("String    ", 10, this::simpleCall);
        SingularTestUtil.performance("Composite ", 10, this::compositeCall);
        SingularTestUtil.performance("String2   ", 20, this::simpleCall);
        SingularTestUtil.performance("Composite2", 20, this::compositeCall);
        SingularTestUtil.performance("String3   ", 20, this::simpleCall);
        SingularTestUtil.performance("Composite3", 20, this::compositeCall);
    }

    private void simpleCall() {
        STypeString type = createTestDictionary().getType(STypeString.class);
        readAttributes(type);
    }

    private void readAttributes(SType<?> type) {
        type.getAttributeValue(SPackageBasic.ATR_LABEL);
        type.getAttributeValue(SPackageBasic.ATR_SUBTITLE);
        type.getAttributeValue(SPackageBasic.ATR_REQUIRED);
        type.getAttributeValue(SPackageBasic.ATR_VISIBLE);
    }

    private void compositeCall() {
        STypeExternalAttributeComposite2 type = createTestDictionary().getType(STypeExternalAttributeComposite2.class);
        readAttributes(type);
        readAttributes(type.getField("field1"));
        readAttributes(type.getField("field2"));
    }

    @Test
    public void loadFormExternalLazy_withClassReadTriger() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeLazyLoad.class));

        AtrRef<STypeString, SIString, String> atr1 = PackageDinamicAttr.ATR_TEXT1;
        AtrRef<STypeInteger, SIInteger, Integer> atr2 = PackageDinamicAttr.ATR_INT1;
        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        type.isAttribute(SPackageBasic.ATR_LABEL, "LBL");
        type.isAttribute(atr1.getNameFull(), "correct");
        type.isAttribute(atr2.getNameFull(), "20");

        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        type.isAttribute(atr1, "correct");
        type.isAttribute(atr2, 20);

        //Depois das linhas a cima, então têm que ter convertido os valores
        type.isAttribute(atr2.getNameFull(), 20);
        assertEquals(SIInteger.class, type.getTarget().getAttributeDirectly(atr2.getNameFull()).get().getClass());

        assertTrue(type.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertTrue(type.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
    }

    @Test
    public void loadFormExternalLazy_withInstanceReadTriger() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSInstance instance = assertInstance(dictionary.newInstance(STypeExternalAttributeLazyLoad.class));

        AtrRef<STypeString, SIString, String> atr1 = PackageDinamicAttr.ATR_TEXT1;
        AtrRef<STypeInteger, SIInteger, Integer> atr2 = PackageDinamicAttr.ATR_INT1;
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        instance.isAttribute(SPackageBasic.ATR_LABEL, "LBL");
        instance.isAttribute(atr1.getNameFull(), "correct");
        instance.isAttribute(atr2.getNameFull(), "20");

        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertFalse(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());

        instance.isAttribute(atr1, "correct");
        instance.isAttribute(atr2, 20);

        //Depois das linhas a cima, então têm que ter convertido os valores
        instance.isAttribute(atr2.getNameFull(), 20);
        assertEquals(SIInteger.class, instance.getTarget().getType().getAttributeDirectly(atr2.getNameFull()).get().getClass());

        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr1.getNameFull()).isPresent());
        assertTrue(instance.getTarget().getDictionary().getTypeOptional(atr2.getNameFull()).isPresent());
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeLazyLoad extends STypeString {
    }

    @SInfoPackage(name = "dinamic")
    public static class PackageDinamicAttr extends SPackage {

        public static final AtrRef<STypeString, SIString, String> ATR_TEXT1 = new AtrRef<>(PackageDinamicAttr.class,
                "text1", STypeString.class, SIString.class, String.class);

        public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_INT1 = new AtrRef<>(PackageDinamicAttr.class,
                "int1", STypeInteger.class, SIInteger.class, Integer.class);

        protected void onLoadPackage(PackageBuilder pb) {
            pb.createAttributeIntoType(SType.class, ATR_TEXT1);
            pb.createAttributeIntoType(SType.class, ATR_INT1);
        }
    }

}
