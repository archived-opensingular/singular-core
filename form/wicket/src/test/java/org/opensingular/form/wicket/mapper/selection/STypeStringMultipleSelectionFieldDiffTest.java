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

package org.opensingular.form.wicket.mapper.selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.diff.DocumentDiff;
import org.opensingular.form.util.diff.DocumentDiffUtil;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.internal.lib.commons.xml.MElement;

import java.io.Serializable;
import java.util.List;

public class STypeStringMultipleSelectionFieldDiffTest implements Serializable {
    private transient SingularFormDummyPageTester page1;
    private transient SingularFormDummyPageTester page2;

    private transient static STypeList fieldType;

    private static void buildBaseType(STypeComposite<?> baseType) {
        fieldType = baseType.addFieldListOf("favoriteFruit", STypeString.class);

        fieldType.withView(SMultiSelectionByCheckboxView::new);
        fieldType.selectionOf("strawberry", "apple", "orange");
    }

    @Test
    public void submitsSelectedValue() {
        page1 = new SingularFormDummyPageTester();
        page1.getDummyPage().setTypeBuilder(STypeStringMultipleSelectionFieldDiffTest::buildBaseType);
        page1.startDummyPage();
        page1.newFormTester()
                .select(getFormRelativePath((FormComponent)
                        page1.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget()), 0)
                .select(getFormRelativePath((FormComponent)
                        page1.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget()), 1)
                .select(getFormRelativePath((FormComponent)
                        page1.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget()), 2)
                .submit();
        List result = (List) page1.getAssertionsForm()
                .getSubComponentWithType(fieldType).assertSInstance().isList(3).getTarget().getValue();


        SInstance instance1 = page1.getAssertionsInstance().getTarget();
        MElement  element   = SFormXMLUtil.toXML(instance1).get();
        element.printTabulado();
        page2 = new SingularFormDummyPageTester();
        page2.getDummyPage().setTypeBuilder(STypeStringMultipleSelectionFieldDiffTest::buildBaseType);
        page2.getDummyPage()
                .setInstanceCreator(refType -> SFormXMLUtil.fromXML(refType, element, page2.getDummyPage().mockFormConfig.getDocumentFactory()));

        page2.startDummyPage();
        page2.newFormTester()
                .selectMultiple(getFormRelativePath((FormComponent)
                        page2.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget()), new int[]{0, 2}, true)
                .submit();
        SInstance instance2 = page2.getAssertionsInstance().getTarget();
        MElement  element2  = SFormXMLUtil.toXML(instance2).get();
        element2.printTabulado();

        DocumentDiff diff = DocumentDiffUtil.calculateDiff(instance1, instance2);
        Assert.assertEquals(1, diff.getQtdChanges());

    }


    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }
}
