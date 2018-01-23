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

package org.opensingular.form.wicket.mapper.list;


import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.util.List;


public class PanelListWithCompositeSelectionTest {

    private static STypeList<STypeComposite<SIComposite>, SIComposite> mockList;
    private static STypeComposite mockTypeComposite;
    private static STypeComposite<?> compositeSelection;
    private static STypeString id;
    private static STypeString description;

    private SingularFormDummyPageTester tester;

    private static void buildBaseType(STypeComposite<?> mockType) {
        mockList = mockType.addFieldListOfComposite("mockList", "mockTypeComposite");
        mockList.asAtr().label("Mock Type Composite");
        mockList.withView(SViewListByForm::new);

        mockTypeComposite = mockList.getElementsType();
        compositeSelection = mockTypeComposite.addFieldComposite("compositeSelection");

        id          = compositeSelection.addFieldString("id");
        description = compositeSelection.addFieldString("description");

        compositeSelection.selection()
                .id(id)
                .display(description)
                .simpleProvider(builder -> {
                    builder.add().set(id, "a").set(description, "v_1");
                    builder.add().set(id, "b").set(description, "v_2");
                    builder.add().set(id, "c").set(description, "v_3");
                });
    }

    @Before
    public void setUp(){
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setAsEditView();
        tester.getDummyPage().setTypeBuilder(PanelListWithCompositeSelectionTest::buildBaseType);
        tester.startDummyPage();
    }

    @Test
    public void testAddItem() {
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(2);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(3);
    }

    @Test
    public void testRemoveItem() {
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        Button removeButton = (Button) tester.getAssertionsForm().findSubComponent(b -> b.getClass().getName().contains("RemoverButton")).getTarget();
        tester.executeAjaxEvent(removeButton, "click");

        Component modalConfirm = tester.getAssertionsForm().findSubComponent(c -> c.getId().equalsIgnoreCase("modal-confirm-btn")).getTarget();
        tester.executeAjaxEvent(modalConfirm, "click");

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);
    }

    @Test
    public void testAddItemAndFillOptions() {
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        List<SIString> listCompositeTypes = (List<SIString>) tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(compositeSelection)
                .assertSInstance().getTarget().getValue();
        listCompositeTypes.get(0).setValue("a");

        tester.newFormTester().submit();

        tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(compositeSelection)
                .assertSInstance().isValueEquals(listCompositeTypes);
    }

    @Test
    public void testAddItemFillOptionsAndThenAddOtherItem() {
        final Button addButton = findAddButton();

        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(0);

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(1);

        List<SIString> listCompositeTypes = (List<SIString>) tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(compositeSelection)
                .assertSInstance().getTarget().getValue();
        listCompositeTypes.get(0).setValue("a");

        tester.executeAjaxEvent(addButton, "click");
        tester.getAssertionsForm().getSubComponentWithType(mockList).assertSInstance().isList(2);

        tester.getAssertionsForm()
                .getSubComponentWithType(mockList)
                .getSubComponentWithType(mockTypeComposite)
                .getSubComponentWithType(compositeSelection)
                .assertSInstance().isValueEquals(listCompositeTypes);
    }

    private Button findAddButton() {
        return (Button) tester.getAssertionsForm()
                .findSubComponent(b -> b.getClass().getName().contains("AddButton")).getTarget();
    }

}
