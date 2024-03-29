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

package org.opensingular.form.wicket.mapper.table;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.list.SViewListByTable;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;
import org.opensingular.form.wicket.mapper.buttons.RemoverButton;

public class TableListWithCompositeTypeTest {

    private SingularFormDummyPageTester tester;

    private static STypeList<STypeComposite<SIComposite>, SIComposite> mockList;
    private static STypeComposite<?> mockTypeComposite;
    private static STypeString simpleString;

    private static void buildBaseType(STypeComposite<?> mockType) {

        mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockTypeComposite = mockList.getElementsType();

        mockList.withView(SViewListByTable::new);
        mockList.asAtr()
                .label("Mock Type Composite");

        simpleString = mockTypeComposite.addFieldString("mockTypeComposite", true);

    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(TableListWithCompositeTypeTest::buildBaseType);
        tester.startDummyPage();
    }

    @Test
    public void testAddItem() {
        final AjaxLink addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(2);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(3);

        tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(simpleString).assertSInstance();
    }

    @Test
    public void testRemoveItem() {
        final AjaxLink addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(simpleString).assertSInstance();

        Button removeButton = tester.getAssertionsForm().findSubComponent(b -> b instanceof RemoverButton).getTarget(Button.class);

        tester.executeAjaxEvent(removeButton, "click");

        Component modalConfirm = tester.getAssertionsForm().findSubComponent(c -> c.getId().equalsIgnoreCase("modal-confirm-btn")).getTarget();
        tester.executeAjaxEvent(modalConfirm, "click");

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);
    }

    @Test
    public void testAddItemAndFillOptions() {
        final AjaxLink addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        final String value = "123456";

        AssertionsWComponent stringAssertion = tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(simpleString);

        stringAssertion.assertSInstance().getTarget().setValue(value);

        tester.newFormTester().submit();

        stringAssertion.assertSInstance().isValueEquals(value);
    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {
        final AjaxLink addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        final String value = "123456";

        AssertionsWComponent stringAssertion = tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(simpleString);

        stringAssertion.assertSInstance().getTarget().setValue(value);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(2);

        stringAssertion.assertSInstance().isValueEquals(value);
    }

    public AjaxLink findAddButton(){
        return tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget(AjaxLink.class);
    }
}
