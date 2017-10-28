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

import org.apache.wicket.markup.html.form.Button;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;


public class TableListWithSimpleTypeTest {

    private static STypeList<STypeString, SIString> nomes;
    private static STypeString elementsType;

    private static SingularDummyFormPageTester tester;

    private static void buildBaseType(STypeComposite<?> mockType){
        nomes = mockType.addFieldListOf("nomes", STypeString.class);
        elementsType = nomes.getElementsType();

        nomes.withView(SViewListByTable::new);
        nomes.asAtr().label("Nomes");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(TableListWithSimpleTypeTest::buildBaseType);
        tester.startDummyPage();
    }

    @Test
    public void testAddItem(){
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(0);

        final Button addButton = findAddButton();

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(1);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(2);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(3);
    }

    @Test
    public void testRemoveItem(){
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(1);

        Button removeButton = tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("RemoverButton")).getTarget(Button.class);

        tester.executeAjaxEvent(removeButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(0);
    }

    @Test
    public void testAddItemAndFillOptions() {
        final Button addButton = findAddButton();
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(1);

        final String newValue = "123456";
        tester.getAssertionsForm().getSubComponentWithType(nomes).getSubComponentWithType(elementsType).assertSInstance().getTarget().setValue(newValue);

        tester.newFormTester().submit();

        tester.getAssertionsForm().getSubComponentWithType(nomes).getSubComponentWithType(elementsType).assertSInstance().isValueEquals("123456");
    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(1);

        String newValue = "123456";
        tester.getAssertionsForm().getSubComponentWithType(nomes).getSubComponentWithType(elementsType).assertSInstance().getTarget().setValue(newValue);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(nomes).assertSInstance().isList(2);

        tester.getAssertionsForm().getSubComponentWithType(nomes).getSubComponentWithType(elementsType).assertSInstance().isValueEquals(newValue);
    }

    public Button findAddButton(){
        return tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget(Button.class);
    }
}
