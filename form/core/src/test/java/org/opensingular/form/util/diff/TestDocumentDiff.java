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

package org.opensingular.form.util.diff;

import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.*;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeA;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeB;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeC;

import java.util.Optional;

/**
 * @author Daniel C. Bordin on 27/12/2016.
 */
@RunWith(Parameterized.class)
public class TestDocumentDiff extends TestCaseForm {

    public TestDocumentDiff(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testSimpleComposite() {
        TestCompositeA typeA = createTestDictionary().getType(TestCompositeA.class);

        SIComposite iA1 = typeA.newInstance();
        SIComposite iA2 = typeA.newInstance();
        iA2.setValue(typeA.name, "Daniel");
        SIComposite iA3 = typeA.newInstance();
        iA3.setValue(typeA.name, "Daniel Bordin");
        iA3.setValue(typeA.age, 41);
        SIComposite iA4 = typeA.newInstance();
        iA4.setValue(typeA.name, "Daniel C Bordin");
        iA4.setValue(typeA.age, 42);
        SIComposite iA5 = typeA.newInstance();
        iA5.setValue(typeA.name, "Daniel C Bordin");
        iA5.setValue(typeA.age, 43);

        DocumentDiff diff;
        diff = calculateDiff(iA1, iA1, 0);
        assertDiffUnchangedEmpty(diff, typeA);
        assertDiffUnchangedEmpty(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeA.age);
        diff = calculateDiff(iA2, iA2, 0);
        assertDiffUnchanged(diff, typeA);
        assertDiffUnchanged(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeA.age);
        diff = calculateDiff(iA3, iA3, 0);
        assertDiffUnchanged(diff, typeA);
        assertDiffUnchanged(diff, typeA.name);
        assertDiffUnchanged(diff, typeA.age);

        diff = calculateDiff(iA1, iA2, 1);
        assertDiffNew(diff, typeA);
        assertDiffNew(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeA.age);

        diff = calculateDiff(iA2, iA1, 1);
        assertDiffDeleted(diff, typeA);
        assertDiffDeleted(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeA.age);

        diff = calculateDiff(iA1, iA3, 2);
        assertDiffNew(diff, typeA);
        assertDiffNew(diff, typeA.name);
        assertDiffNew(diff, typeA.age);
        diff = calculateDiff(iA3, iA1, 2);
        assertDiffDeleted(diff, typeA);
        assertDiffDeleted(diff, typeA.name);
        assertDiffDeleted(diff, typeA.age);
        diff = calculateDiff(iA3, iA4, 2);
        assertDiffChanged(diff, typeA);
        assertDiffChanged(diff, typeA.name);
        assertDiffChanged(diff, typeA.age);
        diff = calculateDiff(iA4, iA3, 2);
        assertDiffChanged(diff, typeA);
        assertDiffChanged(diff, typeA.name);
        assertDiffChanged(diff, typeA.age);
        diff = calculateDiff(iA4, iA5, 1);
        assertDiffChanged(diff, typeA);
        assertDiffUnchanged(diff, typeA.name);
        assertDiffChanged(diff, typeA.age);
    }

    @Test
    public void testCompositeOfDifferentTypes() {
        TestCompositeA typeA = createTestDictionary().getType(TestCompositeA.class);
        TestCompositeB typeB = createTestDictionary().getType(TestCompositeB.class);

        SIComposite iA1 = typeA.newInstance();
        SIComposite iA2 = typeA.newInstance();
        iA2.setValue(typeA.name, "Daniel");
        SIComposite iA3 = typeA.newInstance();
        iA3.setValue(typeA.name, "Daniel Bordin");
        iA3.setValue(typeA.age, 43);

        SIComposite iB1 = typeB.newInstance();
        SIComposite iB2 = typeB.newInstance();
        iB2.setValue(typeB.name, "Daniel");
        SIComposite iB3 = typeB.newInstance();
        iB3.setValue(typeA.name, "Daniel Bordin");
        iB3.setValue(typeB.info, "special");
        SIComposite iB4 = typeB.newInstance();
        iB4.setValue(typeB.name, "Daniel");
        iB4.setValue(typeB.age, 43);
        iB4.setValue(typeB.info, "special");


        DocumentDiff diff;
        diff = calculateDiff(iB1, iB1, 0);
        assertDiffUnchangedEmpty(diff, typeB);
        assertDiffUnchangedEmpty(diff, typeB.name);
        assertDiffUnchangedEmpty(diff, typeB.age);
        assertDiffUnchangedEmpty(diff, typeB.info);

        diff = calculateDiff(iB3, iB3, 0);
        assertDiffUnchanged(diff, typeB);
        assertDiffUnchanged(diff, typeB.name);
        assertDiffUnchangedEmpty(diff, typeB.age);
        assertDiffUnchanged(diff, typeB.info);

        diff = calculateDiff(iA1, iB1, 0);
        assertDiffUnchangedEmpty(diff, typeA);
        assertDiffUnchangedEmpty(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeB.age);
        assertDiffUnchangedEmpty(diff, typeB.info);

        diff = calculateDiff(iA2, iB3, 2);
        assertDiffChanged(diff, typeB);
        assertDiffChanged(diff, typeB.name);
        assertDiffUnchangedEmpty(diff, typeB.age);
        assertDiffNew(diff, typeB.info);

        diff = calculateDiff(iB3, iA1, 2);
        assertDiffDeleted(diff, typeA);
        assertDiffDeleted(diff, typeA.name);
        assertDiffUnchangedEmpty(diff, typeB.age);
        assertDiffDeleted(diff, typeB.info);

        diff = calculateDiff(iB4, iA3, 2);
        assertDiffChanged(diff, typeA);
        assertDiffChanged(diff, typeA.name);
        assertDiffUnchanged(diff, typeB.age);
        assertDiffDeleted(diff, typeB.info);

        diff = calculateDiff(iB4, iB2, 2);
        assertDiffChanged(diff, typeB);
        assertDiffUnchanged(diff, typeB.name);
        assertDiffDeleted(diff, typeB.age);
        assertDiffDeleted(diff, typeB.info);

        diff = calculateDiff(iB2, iB4, 2);
        assertDiffChanged(diff, typeB);
        assertDiffUnchanged(diff, typeB.name);
        assertDiffNew(diff, typeB.age);
        assertDiffNew(diff, typeB.info);
    }

    @Test
    public void testCompositeOfComposite() {
        TestCompositeA typeA = createTestDictionary().getType(TestCompositeA.class);
        TestCompositeB typeB = createTestDictionary().getType(TestCompositeB.class);
        TestCompositeC typeC = createTestDictionary().getType(TestCompositeC.class);

        SIComposite iA3 = typeA.newInstance();
        iA3.setValue(typeA.name, "Daniel Bordin");
        iA3.setValue(typeA.age, 43);

        SIComposite iC1 = typeC.newInstance();
        iC1.setValue("personB.name", "Renato");
        iC1.setValue("personB.age", 40);
        SIComposite iC2 = typeC.newInstance();
        iC2.setValue("personA.name", "Daniel");
        iC2.setValue("personB.name", "Renato");
        iC2.setValue("personB.age", 40);
        SIComposite iC3 = typeC.newInstance();
        iC3.setValue("personA.name", "Daniel Bordin");
        iC3.setValue("personB.name", "Renato");
        iC3.setValue("personB.info", "second");


        DocumentDiff diff;
        diff = calculateDiff(iC1, iC1, 0);
        assertDiffUnchanged(diff, typeC);
        assertDiffUnchangedEmpty(diff, typeC.personA);
        assertDiffUnchanged(diff, typeC.personB);

        diff = calculateDiff(iC1, iC3, 3);
        assertDiffChanged(diff, typeC);
        assertDiffNew(diff, typeC.personA);
        assertDiffNew(diff, "personA.name");
        assertDiffUnchangedEmpty(diff, "personA.age");
        assertDiffChanged(diff, typeC.personB);
        assertDiffUnchanged(diff, "personB.name");
        assertDiffDeleted(diff, "personB.age");
        assertDiffNew(diff, "personB.info");

        diff = calculateDiff(iC3, iC1, 3);
        assertDiffChanged(diff, typeC);
        assertDiffDeleted(diff, typeC.personA);
        assertDiffDeleted(diff, "personA.name");
        assertDiffUnchangedEmpty(diff, "personA.age");
        assertDiffChanged(diff, typeC.personB);
        assertDiffUnchanged(diff, "personB.name");
        assertDiffNew(diff, "personB.age");
        assertDiffDeleted(diff, "personB.info");

        diff = calculateDiff(iA3, iC3, 5);
        assertDiffChanged(diff, typeC);
        assertDiffNew(diff, typeC.personA);
        assertDiffNew(diff, "personA.name");
        assertDiffUnchangedEmpty(diff, "personA.age");
        assertDiffNew(diff, typeC.personB);
        assertDiffNew(diff, "personB.name");
        assertDiffUnchangedEmpty(diff, "personB.age");
        assertDiffNew(diff, "personB.info");
        assertDiffDeleted(diff, "name");
        assertDiffDeleted(diff, "age");

        diff = calculateDiff(iA3, iC3.getField("personA"), 1);
        assertDiffChanged(diff, typeA);
        assertDiffUnchanged(diff, "name");
        assertDiffDeleted(diff, "age");

    }

    @Test
    public void testListSimple() {
        STypeList<STypeString, SIString> typeNomes = createTestDictionary().createNewPackage("test").createListTypeOf(
                "nomes", STypeString.class);

        SIList<SIString> iL1 = typeNomes.newInstance();
        SIList<SIString> iL2 = typeNomes.newInstance();
        iL2.addValue("A");
        SIList<SIString> iL3 = typeNomes.newInstance();
        iL3.addValue("AA");
        SIList<SIString> iL4 = typeNomes.newInstance();
        iL4.addValue("A");
        iL4.addValue("B");
        SIList<SIString> iL5 = typeNomes.newInstance();
        iL5.addValue("A");
        iL5.addValue("B");
        iL5.addValue("C");
        iL5.remove(0);
        SIList<SIString> iL6 = typeNomes.newInstance();
        iL6.addValue("A");
        iL6.addValue("B");
        iL6.addValue("CC");
        iL6.addValue("D");

        DocumentDiff diff;
        diff = calculateDiff(iL1, iL1, 0);
        diff = calculateDiff(iL2, iL2, 0);
        assertDiffUnchanged(diff, "");
        assertDiffUnchanged(diff, "[0]");
        diff = calculateDiff(iL5, iL5, 0);
        assertDiffUnchanged(diff, "");
        assertDiffUnchanged(diff, "[0]");
        assertDiffUnchanged(diff, "[1]");

        diff = calculateDiff(iL1, iL4, 2);
        assertDiffNew(diff, "");
        assertDiffNew(diff, "[0]");
        assertDiffNew(diff, "[1]");

        diff = calculateDiff(iL4, iL1, 2);
        assertDiffDeleted(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffDeleted(diff, "[1]");

        diff = calculateDiff(iL2, iL3, 1);
        assertDiffChanged(diff, "");
        assertDiffChanged(diff, "[0]");

        diff = calculateDiff(iL2, iL5, 3);
        assertDiffChanged(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffNew(diff, "[1]");
        assertDiffNew(diff, "[2]");

        diff = calculateDiff(iL5, iL2, 3);
        assertDiffChanged(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffDeleted(diff, "[1]");
        assertDiffNew(diff, "[2]");

        diff = calculateDiff(iL5, iL6, 3);
        assertDiffChanged(diff, "");
        assertDiffNew(diff, "[0]");
        assertDiffUnchanged(diff, "[1]");
        assertDiffChanged(diff, "[2]");
        assertDiffNew(diff, "[3]");

        diff = calculateDiff(iL6, iL5, 3);
        assertDiffChanged(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffUnchanged(diff, "[1]");
        assertDiffChanged(diff, "[2]");
        assertDiffDeleted(diff, "[3]");
    }

    private void assertType(DiffType expected, DiffInfo info) {
        if (!expected.equals(info.getType())) {
            throw new AssertionFailedError(
                    "Para o item " + info.getOriginalOrNewer() + "\nExpected :" + expected + "\nActual   :" + info.getType());
        }
    }

    private void assertDiffChanged(DocumentDiff diff, SType<?> field) {
        assertType(DiffType.CHANGED_CONTENT, diff.findFirst(field).get());
    }

    private void assertDiffChanged(DocumentDiff diff, String path) {
        assertType(DiffType.CHANGED_CONTENT, diff.get(path));
    }

    private void assertDiffUnchangedEmpty(DocumentDiff diff, SType<?> field) {
        assertType(DiffType.UNCHANGED_EMPTY, diff.findFirst(field).get());
    }

    private void assertDiffUnchangedEmpty(DocumentDiff diff, String path) {
        assertType(DiffType.UNCHANGED_EMPTY, diff.get(path));
    }

    private void assertDiffUnchanged(DocumentDiff diff, SType<?> field) {
        assertType(DiffType.UNCHANGED_WITH_VALUE, diff.findFirst(field).get());
    }

    private void assertDiffUnchanged(DocumentDiff diff, String path) {
        assertType(DiffType.UNCHANGED_WITH_VALUE, diff.get(path));
    }

    private void assertDiffDeleted(DocumentDiff diff, SType<?> field) {
        assertType(DiffType.CHANGED_DELETED, diff.findFirst(field).get());
    }

    private void assertDiffDeleted(DocumentDiff diff, String path) {
        assertType(DiffType.CHANGED_DELETED, diff.get(path));
    }

    private void assertDiffNew(DocumentDiff diff, SType<?> field) {
        assertType(DiffType.CHANGED_NEW, diff.findFirst(field).get());
    }

    private void assertDiffNew(DocumentDiff diff, String path) {
        assertType(DiffType.CHANGED_NEW, diff.get(path));
    }

    private DocumentDiff calculateDiff(SInstance original, SInstance newer, int expectedSize) {
        DocumentDiff diff = DocumentDiffUtil.calculateDiff(original, newer);
        System.out.println("==============");
        diff.debug(true);
        Optional<DiffInfo> compacted = diff.removeUnchangedAndCompact();
        if (compacted.isPresent()) {
            System.out.println("-------------");
            compacted.get().debug();
        }
        assertEquals(expectedSize, diff.getQtdChanges());
        return diff;
    }

    @SInfoPackage(name = "test.diff")
    public static final class TestDiffPackage extends SPackage {

        @Override
        protected void onLoadPackage(PackageBuilder pb) {
            pb.createType(TestCompositeA.class);
            pb.createType(TestCompositeB.class);
            pb.createType(TestCompositeC.class);
        }

        @SInfoType(name = "TestCompositeA", spackage = TestDiffPackage.class)
        public static class TestCompositeA extends STypeComposite<SIComposite> {

            public STypeString name;
            public STypeInteger age;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                name = addFieldString("name");
                age = addFieldInteger("age");
            }
        }

        @SInfoType(name = "TestCompositeB", spackage = TestDiffPackage.class)
        public static final class TestCompositeB extends TestCompositeA {

            public STypeString info;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                info = addFieldString("info");
            }
        }

        @SInfoType(name = "TestCompositeC", spackage = TestDiffPackage.class)
        public static final class TestCompositeC extends STypeComposite<SIComposite> {

            public TestCompositeA personA;
            public TestCompositeB personB;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                personA = addField("personA", TestCompositeA.class);
                personB = addField("personB", TestCompositeB.class);
            }
        }
    }
}
