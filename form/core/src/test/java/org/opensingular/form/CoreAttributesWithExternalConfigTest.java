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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;

/**
 * @author Daniel C. Bordin on 28/04/2017.
 */
@RunWith(Parameterized.class)
public class CoreAttributesWithExternalConfigTest extends TestCaseForm {

    public CoreAttributesWithExternalConfigTest(TestFormConfig testFormConfig) {
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
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeNull.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, null);
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeNull extends STypeString {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_LABEL, "L2");
        }
    }

    @Test
    public void loadFormExternalLazy_withBadPropertiesFiles() {
        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong1.class),
                SingularFormException.class, "key='singular.form.basic.label'");

        SingularTestUtil.assertException(() -> createTestDictionary().getType(STypeExternalAttributeWrong2.class),
                SingularFormException.class, "key='bla@'");
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong1 extends STypeString {
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeWrong2 extends STypeString {
    }

    @Test
    public void loadFromExternalFileForComposite() {
        SDictionary dictionary = createTestDictionary();
        AssertionsSType type = assertType(dictionary.getType(STypeExternalAttributeComposite.class));
        type.isAttribute(SPackageBasic.ATR_LABEL, "LLL1");
        type.isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS1");
        type.field("field1").isAttribute(SPackageBasic.ATR_LABEL, "LLL2");
        type.field("field1").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS2");
        type.field("field2").isAttribute(SPackageBasic.ATR_LABEL, "LLL3");
        type.field("field2").isAttribute(SPackageBasic.ATR_SUBTITLE, "SSS3");
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeComposite extends STypeComposite {
        @Override
        protected void onLoadType(TypeBuilder tb) {
            setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC11");
            addFieldString("field1").setAttributeValue(SPackageBasic.ATR_SUBTITLE, "SC21");
            addField("field2", STypeExternalAttribute1.class);
        }
    }

    @Test
    public void loadFromExternalFileForCompositeWrong() {
        SingularTestUtil.assertException(
                () -> createTestDictionary().getType(STypeExternalAttributeCompositeWrong.class),
                SingularFormException.class, "Não foi encontrado o tipo 'field1000'");
    }

    @SInfoType(spackage = PackageExternalAttr.class)
    public static class STypeExternalAttributeCompositeWrong extends STypeComposite {
        @Override
        protected void onLoadType(TypeBuilder tb) {
            addFieldString("field1");
        }
    }

    @Test
    public void loadFormExternalLazy_withClassReadTriger() {

    }

    @Test
    public void loadFormExternalLazy_withInstanceReadTriger() {

    }
}
