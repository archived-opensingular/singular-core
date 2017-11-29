/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.type.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.internal.lib.commons.xml.MParser;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeListTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite> baseType;
    private STypeString name, content;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;

    public STypeListTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        PackageBuilder pkt = createTestPackage();
        baseType = pkt.createCompositeType("baseType");
        name = baseType.addFieldString("name");
        listType = baseType.addFieldListOfComposite("listField", "subStuff");
        content = listType.getElementsType().addFieldString("content");
    }

    @Test
    public void setCompositeValue() throws Exception {
        SIComposite original = baseType.newInstance();
        original.getDescendant(name).setValue("My first name");
        SIComposite e1 = original.getDescendant(listType).addNew();
        e1.getDescendant(content).setValue("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(listType));

        assertThat(backup).doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValue("My second name");
        e1.getDescendant(content).setValue("My second content");

        assertThat(xml(original)).contains("My second name").contains("My second content");

        SIList<SIComposite> fromBackup = SFormXMLUtil.fromXML(listType, MParser.parse(backup));
        original.getDescendant(listType).setValue(fromBackup);

        assertThat(xml(original)).contains("My second name").contains("My first content");

    }

    private String xml(SInstance original) {
        return SFormXMLUtil.toXML(original).get().toString();
    }

    @Test public void aNewListIsEmpty() throws Exception{
        SIList<SIComposite> list = listType.newInstance();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test public void listHelpers() throws Exception{
        SIList<SIComposite> list = listType.newInstance();
        SIComposite e1 = list.addNew();
        e1.setValue(content,"abacate");
        SIComposite e2 = list.addNew();
        e2.setValue(content,"avocado");
        SIComposite e3 = list.addNew();
        e3.setValue(content,"guaca");

        assertThat(list.first()).isEqualTo(e1);
        assertThat(list.last()).isEqualTo(e3);
        list.remove(e1);
        assertThat(list.first()).isEqualTo(e2);
    }
}
