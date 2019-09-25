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
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.io.HashUtil;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeA;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeB;
import org.opensingular.form.util.diff.TestDocumentDiff.TestDiffPackage.TestCompositeC;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.IOException;
import java.util.Collections;

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
    public void testEmptyCompositeType() {
        STypeComposite emptyType = createTestDictionary().getType(STypeComposite.class);

        SIComposite emptyInstance1 = (SIComposite) emptyType.newInstance();
        SIComposite emptyInstance2 = (SIComposite) emptyType.newInstance();

        DocumentDiff diff;
        diff = calculateDiff(emptyInstance1, emptyInstance1, 0);
        assertDiffUnchangedEmpty(diff, emptyType);

        diff = calculateDiff(emptyInstance1, emptyInstance2, 0);
        assertDiffUnchangedEmpty(diff, emptyType);


        diff = calculateDiff(emptyInstance2, emptyInstance1, 0);
        assertDiffUnchangedEmpty(diff, emptyType);
    }

    @Test
    public void testEmptyCompositeDifferentType() {
        STypeComposite emptyType = createTestDictionary().getType(STypeComposite.class);
        TestCompositeB typeB = createTestDictionary().getType(TestCompositeB.class);

        SIComposite emptyInstance1 = (SIComposite) emptyType.newInstance();
        SIComposite iB1 = typeB.newInstance();
        iB1.setValue(typeB.name, "Daniel");
        iB1.setValue(typeB.info, "special");

        DocumentDiff diff;
        diff = calculateDiff(emptyInstance1, iB1, 2);
        assertDiffNew(diff, typeB.name);
        assertDiffNew(diff, typeB.info);

        diff = calculateDiff(iB1, emptyInstance1, 2);
        assertDiffDeleted(diff, typeB.name);
        assertDiffDeleted(diff, typeB.info);


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
        AssertionsDiff compact;

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
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(2).isOriginal(iC3).isNewer(iC1);
        compact.get(0).assertDeleted(0).isOriginal(iC3.getField("personA.name"));
        compact.get(1).assertChanged(2).isOriginal(iC3.getField("personB"));
        compact.get(1).get(0).assertNew(0).isNewer(iC1.getField("personB.age"));
        compact.get(1).get(1).assertDeleted(0).isOriginal(iC3.getField("personB.info"));

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
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(4).isNewer(iC3);
        compact.get(0).assertNew(0).isNewer(iC3.getField("personA.name"));
        compact.get(1).assertNew(2).isNewer(iC3.getField("personB"));
        compact.get(1).get(0).assertNew(0).isNewer(iC3.getField("personB.name"));
        compact.get(1).get(1).assertNew(0).isNewer(iC3.getField("personB.info"));
        compact.get(2).assertDeleted(0).isOriginal(iA3.getField("name"));
        compact.get(3).assertDeleted(0).isOriginal(iA3.getField("age"));

        diff = calculateDiff(iA3, iC3.getField("personA"), 1);
        assertDiffChanged(diff, typeA);
        assertDiffUnchanged(diff, "name");
        assertDiffDeleted(diff, "age");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertDeleted(0).isOriginal(iA3.getField("age"));
        compact.assertNames("age", "Age", "personA.age", "Person A : Age");

    }

    @Test
    public void testCompositeResumedBecauseOfView() {
        TestCompositeC typeC = createTestDictionary().getType(TestCompositeC.class);
        typeC.getField(typeC.personB).asAtrProvider().provider(ctx -> Collections.emptyList());

        SIComposite c0 = typeC.newInstance();
        c0.setValue("personA.name", "Lara");
        c0.setValue("personA.age", 7);
        SIComposite c1 = typeC.newInstance();
        c1.setValue("personA.name", "Daniel");
        c1.setValue("personA.age", 40);
        c1.setValue("personB.name", "Marcos");
        c1.setValue("personB.age", 20);
        SIComposite c2 = typeC.newInstance();
        c2.setValue("personA.name", "Daniel B");
        c2.setValue("personA.age", 41);
        c2.setValue("personB.name", "Marcos B");
        c2.setValue("personB.age", 21);

        AssertionsDiff diff;
        diff = diff(c1, c2, 4).assertChanged(2);
        diff.get(0).assertChanged(2);
        diff.get(0).get(0).assertChanged(0);
        diff.get(0).get(1).assertChanged(0);
        diff.get(1).assertChanged(3);
        diff.get(1).get(0).assertChanged(0);
        diff.get(1).get(1).assertChanged(0);
        diff.get(1).get(2).assertUnchangedEmpty(0);
        diff = diff.compact(3);
        diff.get(0).assertChanged(2);
        diff.get(0).get(0).assertChanged(0);
        diff.get(0).get(1).assertChanged(0);
        diff.get(1).assertChanged(0);

        diff = diff(c0, c2, 4).assertChanged(2);
        diff.get(0).assertChanged(2);
        diff.get(0).get(0).assertChanged(0);
        diff.get(0).get(1).assertChanged(0);
        diff.get(1).assertNew(3);
        diff.get(1).get(0).assertNew(0);
        diff.get(1).get(1).assertNew(0);
        diff.get(1).get(2).assertUnchangedEmpty(0);
        diff = diff.compact(3);
        diff.get(0).assertChanged(2);
        diff.get(0).get(0).assertChanged(0);
        diff.get(0).get(1).assertChanged(0);
        diff.get(1).assertNew(0);
    }

    @Test
    public void testListSimple() {
        STypeList<STypeString, SIString> typeNomes = createTestPackage().createListTypeOf("nomes", STypeString.class);

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
        AssertionsDiff compact;

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
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(0).isNewer(iL3.get(0)).isIndex(0, 0);


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
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(3).isNewer(iL6);
        compact.get(0).assertNew(0).isNewer(iL6.get(0)).isIndex(-1, 0);
        compact.get(1).assertChanged(0).isNewer(iL6.get(2)).isIndex(1, 2);
        compact.get(2).assertNew(0).isNewer(iL6.get(3)).isIndex(-1, 3);
        compact.get(0).assertNames("String", null, "[ >0]", "Linha nova");
        compact.get(1).assertNames("String", null, "[1>2]", "Linha 2");
        compact.get(2).assertNames("String", null, "[ >3]", "Linha nova");

        diff = calculateDiff(iL6, iL5, 3);
        assertDiffChanged(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffUnchanged(diff, "[1]");
        assertDiffChanged(diff, "[2]");
        assertDiffDeleted(diff, "[3]");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(3).isOriginal(iL6);
        compact.get(0).assertDeleted(0).isOriginal(iL6.get(0));
        compact.get(1).assertChanged(0).isOriginal(iL6.get(2));
        compact.get(2).assertDeleted(0).isOriginal(iL6.get(3));
        compact.get(0).assertNames("String", null, "[0> ]", "Linha 1");
        compact.get(1).assertNames("String", null, "[2>1]", "Linha 3");
        compact.get(2).assertNames("String", null, "[3> ]", "Linha 4");
    }

    @Test
    public void testListOfComposite() {
        PackageBuilder pkg = createTestPackage();
        STypeList<TestCompositeC, SIComposite> typeCs = pkg.createListTypeOf("Cs", TestCompositeC.class);
        typeCs.asAtr().label("Clients");

        SIComposite c;
        SIList<SIComposite> iLC0 = typeCs.newInstance();
        SIList<SIComposite> iLC2 = typeCs.newInstance();
        c = iLC2.addNew();
        c.setValue("personA.name", "Daniel");
        SIList<SIComposite> iLC3 = typeCs.newInstance();
        c = iLC3.addNew();
        c.setValue("personA.name", "Daniel");
        c = iLC3.addNew();
        c.setValue("personA.name", "Renato");
        c.setValue("personB.name", "Lara");
        SIList<SIComposite> iLC4 = typeCs.newInstance();
        c = iLC4.addNew();
        c.setValue("personA.name", "Daniel");
        c = iLC4.addNew();
        c.setValue("personA.name", "Renato");
        c.setValue("personB.name", "Lara2");
        c = iLC4.addNew();
        c.setValue("personB.name", "Tomas");
        iLC4.remove(0);

        DocumentDiff diff;
        AssertionsDiff compact;
        diff = calculateDiff(iLC0, iLC0, 0);

        diff = calculateDiff(iLC2, iLC2, 0);
        assertDiffUnchanged(diff, "");
        assertDiffUnchanged(diff, "[0]");
        assertDiffUnchanged(diff, "[0].personA");
        assertDiffUnchanged(diff, "[0].personA.name");
        assertDiffUnchangedEmpty(diff, "[0].personA.age");
        assertDiffUnchangedEmpty(diff, "[0].personB");
        diff = calculateDiff(iLC3, iLC3, 0);

        diff = calculateDiff(iLC0, iLC2, 1);
        assertDiffNew(diff, "");
        assertDiffNew(diff, "[0]");
        assertDiffNew(diff, "[0].personA");
        assertDiffNew(diff, "[0].personA.name");
        assertDiffUnchangedEmpty(diff, "[0].personA.age");
        assertDiffUnchangedEmpty(diff, "[0].personB");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertNew(0).isNewer(iLC2.getField("[0]"));

        diff = calculateDiff(iLC0, iLC3, 3);
        assertDiffNew(diff, "");
        assertDiffNew(diff, "[0]");
        assertDiffNew(diff, "[0].personA");
        assertDiffNew(diff, "[0].personA.name");
        assertDiffUnchangedEmpty(diff, "[0].personA.age");
        assertDiffUnchangedEmpty(diff, "[0].personB");
        assertDiffNew(diff, "[1]");
        assertDiffNew(diff, "[1].personA");
        assertDiffNew(diff, "[1].personA.name");
        assertDiffUnchangedEmpty(diff, "[1].personA.age");
        assertDiffNew(diff, "[1].personB");
        assertDiffNew(diff, "[1].personB.name");
        assertDiffUnchangedEmpty(diff, "[1].personB.age");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertNew(2).isNewer(iLC3);
        compact.get(0).assertNew(0).isNewer(iLC3.get(0));
        compact.get(1).assertNew(0).isNewer(iLC3.get(1));

        diff = calculateDiff(iLC3, iLC0, 3);
        assertDiffDeleted(diff, "");
        assertDiffDeleted(diff, "[0]");
        assertDiffDeleted(diff, "[0].personA");
        assertDiffDeleted(diff, "[0].personA.name");
        assertDiffUnchangedEmpty(diff, "[0].personA.age");
        assertDiffUnchangedEmpty(diff, "[0].personB");
        assertDiffDeleted(diff, "[1]");
        assertDiffDeleted(diff, "[1].personA");
        assertDiffDeleted(diff, "[1].personA.name");
        assertDiffUnchangedEmpty(diff, "[1].personA.age");
        assertDiffDeleted(diff, "[1].personB");
        assertDiffDeleted(diff, "[1].personB.name");
        assertDiffUnchangedEmpty(diff, "[1].personB.age");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertDeleted(2).isOriginal(iLC3);
        compact.get(0).assertDeleted(0).isOriginal(iLC3.get(0));
        compact.get(1).assertDeleted(0).isOriginal(iLC3.get(1));

        diff = calculateDiff(iLC2, iLC3, 2);
        assertDiffChanged(diff, "");
        assertDiffUnchanged(diff, "[0]");
        assertDiffUnchanged(diff, "[0].personA");
        assertDiffUnchanged(diff, "[0].personA.name");
        assertDiffUnchangedEmpty(diff, "[0].personA.age");
        assertDiffUnchangedEmpty(diff, "[0].personB");
        assertDiffNew(diff, "[1]");
        assertDiffNew(diff, "[1].personA");
        assertDiffNew(diff, "[1].personA.name");
        assertDiffUnchangedEmpty(diff, "[1].personA.age");
        assertDiffNew(diff, "[1].personB");
        assertDiffNew(diff, "[1].personB.name");
        assertDiffUnchangedEmpty(diff, "[1].personB.age");
        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertNew(0).isNewer(iLC3.get(1)).isIndex(-1, 1);
        compact.assertNames("TestCompositeC", "Composite C", "Cs[ >1]", "Clients : Linha nova");

        diff = calculateDiff(iLC3, iLC4, 3);

        compact = assertDiff(diff.removeUnchangedAndCompact());
        compact.assertChanged(3).isOriginal(iLC3).isNewer(iLC4);
        compact.get(0).assertDeleted(0).isOriginal(iLC3.get(0)).isIndex(0, -1);
        compact.get(1).assertChanged(0).isOriginal(iLC3.getField("[1].personB.name"));
        compact.get(2).assertNew(0).isOriginal(null).isNewer(iLC4.get(1)).isIndex(-1, 1);
        compact.get(0).assertNames("TestCompositeC", "Composite C", "[0> ]", "Linha 1");
        compact.get(1).assertNames("name", "Name", "[1>0].personB.name", "Linha 2 : Person B : Name");
        compact.get(2).assertNames("TestCompositeC", "Composite C", "[ >1]", "Linha nova");

    }

    @Test
    public void testListOfCompositeEmpty() {
        PackageBuilder pkg = createTestPackage();
        STypeList<TestCompositeC, SIComposite> typeCs = pkg.createListTypeOf("Cs", TestCompositeC.class);

        SIList<SIComposite>[] iL = new SIList[6];

        SIComposite c;
        iL[0] = typeCs.newInstance();
        iL[1] = typeCs.newInstance();
        c = iL[1].addNew();
        iL[2] = typeCs.newInstance();
        c = iL[2].addNew();
        c = iL[2].addNew();
        iL[3] = typeCs.newInstance();
        c = iL[3].addNew();
        c = iL[3].addNew();
        iL[3].remove(0);
        iL[4] = typeCs.newInstance();
        c = iL[4].addNew();
        c.getField("personA.name");


        for (int i = 0; i < iL.length; i++) {
            for (int j = 0; j < iL.length; j++) {
                if (iL[i] != null && iL[j] != null) {
                    DocumentDiff diff = calculateDiff(iL[i], iL[j], 0);
                    assertFalse(diff.removeUnchangedAndCompact().hasChange());
                }
            }
        }
    }

    @Test
    public void testAttachment() throws IOException {
        TempFileProvider.create(this, tmpProvider -> {
            PackageBuilder pkg = createTestPackage();
            STypeComposite<SIComposite> typeOrder = pkg.createCompositeType("order");
            STypeAttachment typeFile1 = typeOrder.addFieldAttachment("file", false);
            STypeAttachment typeFile2 = pkg.getDictionary().getType(STypeAttachment.class);

            byte[] content0 = new byte[]{};
            byte[] content1 = new byte[]{10, 20, 30};
            byte[] content2 = new byte[]{1, 2, 3, 4};

            SIAttachment fS20 = typeFile2.newInstance();
            SIAttachment fS21 = typeFile2.newInstance();
            fS21.setContent("f0", tmpProvider.createTempFile(content1), content1.length, HashUtil.toSHA1Base16(content1));
            SIAttachment fS22 = typeFile2.newInstance();
            fS22.setContent("f0", tmpProvider.createTempFile(content1), content1.length, HashUtil.toSHA1Base16(content1));
            SIAttachment fS23 = typeFile2.newInstance();
            fS23.setContent("f00", tmpProvider.createTempFile(content1), content1.length, HashUtil.toSHA1Base16(content1));
            SIAttachment fS24 = typeFile2.newInstance();
            fS24.setContent("f0", tmpProvider.createTempFile(content2), content2.length, HashUtil.toSHA1Base16(content2));
            SIAttachment fS25 = typeFile2.newInstance();
            fS25.setContent("f0", tmpProvider.createTempFile(content2), content2.length, HashUtil.toSHA1Base16(content2));
            fS25.clearInstance();

            AssertionsDiff diff;
            diff = diff(fS20, fS20, 0).assertUnchangedEmpty(0).compact(0).assertUnchangedEmpty(0);
            diff = diff(fS20, null, 0).assertUnchangedEmpty(0).compact(0).assertUnchangedEmpty(0);
            diff = diff(null, fS20, 0).assertUnchangedEmpty(0).compact(0).assertUnchangedEmpty(0);
            diff = diff(fS21, fS21, 0).assertUnchanged(0).compact(0).assertUnchanged(0);
            diff = diff(null, fS21, 1).assertNew(0).compact(1).assertNew(0);
            diff = diff(fS20, fS21, 1).assertNew(0).compact(1).assertNew(0);
            diff = diff(fS21, fS20, 1).assertDeleted(0).compact(1).assertDeleted(0);
            diff = diff(fS21, null, 1).assertDeleted(0).compact(1).assertDeleted(0);

            diff = diff(fS21, fS22, 0).assertUnchanged(0).compact(0).assertUnchanged(0);

            diff = diff(fS21, fS23, 1).assertChanged(0).compact(1).assertChanged(0);
            diff = diff(fS21, fS24, 1).assertChanged(0).compact(1).assertChanged(0);
            diff = diff(fS23, fS24, 1).assertChanged(0).compact(1).assertChanged(0);

            diff = diff(fS20, fS25, 0).assertUnchangedEmpty(0).compact(0).assertUnchangedEmpty(0);

            SIComposite o1 = typeOrder.newInstance();
            SIComposite o2 = typeOrder.newInstance();
            o2.getField(typeFile1).setContent("f0", tmpProvider.createTempFile(content1), content1.length, HashUtil.toSHA1Base16(content1));
            SIComposite o3 = typeOrder.newInstance();
            o3.getField(typeFile1).setContent("f1", tmpProvider.createTempFile(content2), content2.length, HashUtil.toSHA1Base16(content2));
            o3.clearInstance();

            diff = diff(o1, o1, 0).assertUnchangedEmpty(1);
            diff.get(0).assertUnchangedEmpty(0);
            diff.compact(0).assertUnchangedEmpty(0);

            diff = diff(o1, o2, 1).assertNew(1);
            diff.get(0).assertNew(0).isNewer(o2.getField("file"));
            diff.compact(1).assertNew(0).isNewer(o2.getField("file"));

            diff = diff(o1, o3, 0).assertUnchangedEmpty(1);
            diff.get(0).assertUnchangedEmpty(0);
            diff.compact(0).assertUnchangedEmpty(0);

            diff = diff(o2, o3, 1).assertDeleted(1);
            diff.get(0).assertDeleted(0).isOriginal(o2.getField("file"));
            diff.compact(1).assertDeleted(0).isOriginal(o2.getField("file"));
        });
    }

    private void assertDiffNew(DiffInfo info, int expectedChildren) {
        assertDiff(info, expectedChildren, DiffType.CHANGED_NEW);
    }

    private void assertDiffDeleted(DiffInfo info, int expectedChildren) {
        assertDiff(info, expectedChildren, DiffType.CHANGED_DELETED);
    }

    private static void assertDiff(DiffInfo info, int expectedChildren, DiffType expectedType) {
        assertNotNull(info);
        assertType(expectedType, info);
        if (expectedChildren != info.getChildren().size()) {
            throw new AssertionFailedError(
                    "Era esperado " + expectedChildren + " filhos mas foram encontrados " + info.getChildren().size() +
                            " para o diff de " + info.getOriginalOrNewer().getPathFull());
        }
    }

    @Test
    public void testSerialization() {
        TestCompositeC typeC = createTestDictionary().getType(TestCompositeC.class);

        SIComposite iC1 = typeC.newInstance();
        iC1.setValue("personB.name", "Renato");
        iC1.setValue("personB.age", 40);
        SIComposite iC3 = typeC.newInstance();
        iC3.setValue("personA.name", "Daniel Bordin");
        iC3.setValue("personB.name", "Renato");
        iC3.setValue("personB.info", "second");

        AssertionsDiff diff = diff(iC3, iC1, 3).assertChanged(2);
        diff = diff.compact(3).assertChanged(2);

        AssertionsDiff diff2 = new AssertionsDiff(SingularIOUtils.serializeAndDeserialize(diff.getDocumentDiff()));
        diff2.assertChangedWithInstancesNull(2).assertNames("TestCompositeC", "Composite C");
        diff2.get(0).assertDeletedWithInstancesNull(0).assertNames("name", "Name", "personA.name", "Person A : Name");
        diff2.get(1).assertChangedWithInstancesNull(2).assertNames("personB", "Person B");
        diff2.get(1).get(0).assertNewWithInstancesNull(0).assertNames("age", "Age");
        diff2.get(1).get(1).assertDeletedWithInstancesNull(0).assertNames("info", "Info");

    }

    private static void assertType(DiffType expected, DiffInfo info) {
        if (!expected.equals(info.getType())) {
            throw new AssertionFailedError(
                    "Para o item " + info.getOriginalOrNewer() + "\nExpected :" + expected + "\nActual   :" +
                            info.getType());
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


    private AssertionsDiff diff(SInstance original, SInstance newer, int expectedChangesSize) {
        return new AssertionsDiff(calculateDiff(original, newer)).assertChangesSize(expectedChangesSize);
    }

    private DocumentDiff calculateDiff(SInstance original, SInstance newer, int expectedSize) {
        DocumentDiff diff = calculateDiff(original, newer);
        assertEquals(expectedSize, diff.getQtdChanges());
        return diff;
    }

    private DocumentDiff calculateDiff(SInstance original, SInstance newer) {
        DocumentDiff diff = DocumentDiffUtil.calculateDiff(original, newer);
        //debug(diff);
        return diff;
    }

    private void debug(DocumentDiff diff) {
        System.out.println("==============");
        diff.debug();
        DocumentDiff compacted = diff.removeUnchangedAndCompact();
        if (compacted.hasChange()) {
            System.out.println("-------------");
            compacted.debug();
        }
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

                asAtr().label("Person");
                name.asAtr().label("Name");
                age.asAtr().label("Age");
            }
        }

        @SInfoType(name = "TestCompositeB", spackage = TestDiffPackage.class)
        public static final class TestCompositeB extends TestCompositeA {

            public STypeString info;

            @Override
            protected void onLoadType(TypeBuilder tb) {
                info = addFieldString("info");

                asAtr().label("Person of B");
                info.asAtr().label("Info");
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

                asAtr().label("Composite C");
                personA.asAtr().label("Person A");
                personB.asAtr().label("Person B");
            }
        }
    }

    private static AssertionsDiff assertDiff(DocumentDiff documentDiff) {
        return new AssertionsDiff(documentDiff);
    }

    private static final class AssertionsDiff {
        private final DocumentDiff documentDiff;
        private final DocumentDiff documentDiffOriginal;
        private final DiffInfo info;

        public AssertionsDiff(DocumentDiff documentDiff) {
            this(documentDiff, documentDiff.getDiffRoot(), null);
        }

        private AssertionsDiff(DocumentDiff documentDiff, DiffInfo info, DocumentDiff documentDiffOrginal) {
            this.documentDiff = documentDiff;
            this.info = info;
            this.documentDiffOriginal = documentDiffOrginal;
        }

        public DocumentDiff getDocumentDiff() {
            return documentDiff;
        }

        public DiffInfo getInfo() {
            return info;
        }

        public AssertionsDiff assertNew(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_NEW);
            assertNotNull(info.getNewer());
            return this;
        }

        public AssertionsDiff assertNewWithInstancesNull(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_NEW);
            return assertBothInstancesNull();
        }

        public AssertionsDiff assertDeleted(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_DELETED);
            assertNotNull(info.getOriginal());
            return this;
        }

        public AssertionsDiff assertDeletedWithInstancesNull(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_DELETED);
            return assertBothInstancesNull();
        }

        public AssertionsDiff assertUnchanged(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.UNCHANGED_WITH_VALUE);
            return this;
        }

        public AssertionsDiff assertUnchangedEmpty(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.UNCHANGED_EMPTY);
            return this;
        }

        public AssertionsDiff assertChanged(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_CONTENT);
            assertNotNull(info.getOriginal());
            assertNotNull(info.getNewer());
            return this;
        }

        private AssertionsDiff assertBothInstancesNull() {
            assertNull(info.getOriginal());
            assertNull(info.getNewer());
            return this;
        }

        public AssertionsDiff assertChangedWithInstancesNull(int expectedChildrenSize) {
            assertDiff(info, expectedChildrenSize, DiffType.CHANGED_CONTENT);
            return assertBothInstancesNull();
        }

        public AssertionsDiff get(int childIndex) {
            return new AssertionsDiff(documentDiff, info.get(childIndex), null);
        }

        public AssertionsDiff isOriginal(SInstance expectedInstance) {
            assertSame(expectedInstance, info.getOriginal());
            return this;
        }

        public AssertionsDiff isNewer(SInstance expectedInstance) {
            assertSame(expectedInstance, info.getNewer());
            return this;
        }

        public AssertionsDiff isIndex(int expectedOriginalIndex, int expectedNewerIndex) {
            assertEquals(expectedOriginalIndex, info.getOriginalIndex());
            assertEquals(expectedNewerIndex, info.getNewerIndex());
            return this;
        }

        public AssertionsDiff assertChangesSize(int expectedChangesSize) {
            if (expectedChangesSize == 0 && info != null) {
                assertEquals(expectedChangesSize, info.getQtdChanges());
            }
            return this;
        }

        public AssertionsDiff compact(int expectedChangesSize) {
            DocumentDiff compact = documentDiff.removeUnchangedAndCompact();
            AssertionsDiff a = new AssertionsDiff(compact, compact.getDiffRoot(), documentDiff);
            return a.assertChangesSize(expectedChangesSize);
        }

        public AssertionsDiff assertNames(String expectedSimpleName, String expectedSimpleLabel) {
            return assertNames(expectedSimpleName, expectedSimpleLabel, expectedSimpleName, expectedSimpleLabel);
        }

        public AssertionsDiff assertNames(String expectedSimpleName, String expectedSimpleLabel, String expectedName,
                                          String expectedLabel) {
            assertEquals(expectedSimpleName, info.getSimpleName());
            assertEquals(expectedSimpleLabel, info.getSimpleLabel());
            assertEquals(expectedName, info.getName());
            assertEquals(expectedLabel, info.getLabel());
            return this;
        }
    }
}
