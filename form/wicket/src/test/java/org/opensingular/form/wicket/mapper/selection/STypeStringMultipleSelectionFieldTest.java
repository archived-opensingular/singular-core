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
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class STypeStringMultipleSelectionFieldTest {
    private SingularDummyFormPageTester tester;

    private static STypeList fieldType;

    private static void buildBaseType(STypeComposite<?> baseType) {
        fieldType = baseType.addFieldListOf("favoriteFruit", STypeString.class);

        fieldType.withView(SMultiSelectionByCheckboxView::new);
        fieldType.selectionOf("strawberry", "apple", "orange");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeStringMultipleSelectionFieldTest::buildBaseType);
    }

    @Test
    public void renders(){
        tester.startDummyPage();
        tester.assertEnabled(
                tester.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget().getPageRelativePath());

        tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).hasSize(1);
    }

    @Test
    public void rendersAListWithSpecifiedOptions() {
        tester.startDummyPage();
        CheckBoxMultipleChoice choices = tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).element(
                0).getTarget(CheckBoxMultipleChoice.class);

        List<String> chaves   = new ArrayList<>();
        List<String> displays = new ArrayList<>();

        for (Object choice : choices.getChoices()) {
            chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
            displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
        }

        assertThat(chaves).containsExactly("strawberry", "apple", "orange");
        assertThat(displays).containsExactly("strawberry", "apple", "orange");
    }

    @Test
    public void submitsSelectedValue() {
        tester.startDummyPage();
        tester.newFormTester()
                .select(getFormRelativePath((FormComponent)
                        tester.getAssertionsForm().getSubComponentWithId("favoriteFruit").getTarget()), 2)
                .submit();
        List result = (List) tester.getAssertionsForm()
                .getSubComponentWithType(fieldType).assertSInstance().isList(1).getTarget().getValue();
        assertThat(result).containsOnly("orange");
    }

    @Test
    public void rendersAListWithDanglingOptions() {
        tester.getDummyPage().addInstancePopulator(instance ->{
            SIList field = (SIList) instance.getField(fieldType.getNameSimple());
            SInstance element = field.addNew();
            element.setValue("avocado");
        });
        tester.startDummyPage();

        CheckBoxMultipleChoice choices = tester.getAssertionsForm().getSubComponents(CheckBoxMultipleChoice.class).element(
                0).getTarget(CheckBoxMultipleChoice.class);
        List<String> chaves = new ArrayList<>();
        List<String> displays = new ArrayList<>();

        for (Object choice : choices.getChoices()) {
            chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
            displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
        }

        assertThat(chaves).containsExactly("avocado", "strawberry", "apple", "orange");
        assertThat(displays).containsExactly("avocado", "strawberry", "apple", "orange");
    }

    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }
}
