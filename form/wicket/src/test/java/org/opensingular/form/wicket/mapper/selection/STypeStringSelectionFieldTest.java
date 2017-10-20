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
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeStringSelectionFieldTest {
    private SingularDummyFormPageTester tester;

    private static STypeString selectType;

    private static void buildBaseType(STypeComposite<?> baseType) {
        selectType = baseType.addFieldString("favoriteFruit");
        selectType.selectionOf("strawberry", "apple", "orange", "banana");
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeStringSelectionFieldTest::buildBaseType);
    }

    @Test
    public void rendersAnDropDownWithSpecifiedOptions() {
        tester.startDummyPage();
        tester.assertEnabled(tester.getAssertionsForm()
                .getSubCompomentWithId("favoriteFruit").getTarget().getPageRelativePath());

        tester.newFormTester().submit();

        DropDownChoice choices = containsOnlyOneDropDownAndReturnIfFinds();
        List<String> chaves   = new ArrayList<>();
        List<String> displays = new ArrayList<>();
        getValuesOfDropDownIfExists(chaves, displays);

        assertThat(chaves).containsExactly("strawberry", "apple", "orange", "banana");
        assertThat(displays).containsExactly("strawberry", "apple", "orange", "banana");
    }

    @Test
    public void submitsSelectedValue() {
        tester.startDummyPage();

        tester.newFormTester()
                .select(getFormRelativePath((FormComponent)
                        tester.getAssertionsForm().getSubCompomentWithId("favoriteFruit").getTarget()), 2)
                .submit();

        tester.getAssertionsForm().getSubCompomentWithType(selectType).assertSInstance().isValueEquals("orange");
    }

    private String getFormRelativePath(FormComponent component) {
        return component.getPath().replace(component.getForm().getRootForm().getPath() + ":", StringUtils.EMPTY);
    }

    @Test
    public void hasADefaultProvider() {
        tester.startDummyPage();

        DropDownChoice choices = containsOnlyOneDropDownAndReturnIfFinds();
        List<String> chaves   = new ArrayList<>();
        List<String> displays = new ArrayList<>();
        getValuesOfDropDownIfExists(chaves, displays);

        assertThat(chaves).containsExactly("strawberry", "apple", "orange", "banana");
        assertThat(displays).containsExactly("strawberry", "apple", "orange", "banana");
    }

    @Test
    public void rendersAnDropDownWithDanglingOptions() {
        tester.getDummyPage().addInstancePopulator(instance ->instance.setValue(selectType.getNameSimple(), "avocado"));
        tester.startDummyPage();

        List<String> chaves   = new ArrayList<>();
        List<String> displays = new ArrayList<>();
        getValuesOfDropDownIfExists(chaves, displays);

        assertThat(chaves).containsExactly("avocado", "strawberry", "apple", "orange", "banana");
        assertThat(displays).containsExactly("avocado", "strawberry", "apple", "orange", "banana");
    }

    private void getValuesOfDropDownIfExists(List<String> chaves, List<String> displays) {
        DropDownChoice choices = containsOnlyOneDropDownAndReturnIfFinds();
        for (Object choice : choices.getChoices()) {
            chaves.add(choices.getChoiceRenderer().getIdValue(choice, choices.getChoices().indexOf(choice)));
            displays.add(String.valueOf(choices.getChoiceRenderer().getDisplayValue(choice)));
        }
    }

    private DropDownChoice containsOnlyOneDropDownAndReturnIfFinds() {
        return tester.getAssertionsForm()
                .getSubComponents(DropDownChoice.class).isSize(1).get(0).getTarget(DropDownChoice.class);
    }
}
