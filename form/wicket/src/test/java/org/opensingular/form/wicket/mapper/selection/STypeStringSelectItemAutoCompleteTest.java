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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class STypeStringSelectItemAutoCompleteTest {

    private static final String[] OPTIONS = {"Bruce Wayne", "Clark Kent", "Wally West", "Oliver Queen"};
    private static final String[] KEYS = {"Batman", "Superman", "Flash", "Green Arrow"};

    private static STypeString base;

    private SingularDummyFormPageTester tester;

    private static void buildBaseType(STypeComposite<?> baseType) {
        base = baseType.addFieldString("myHero");
        base.autocompleteOf(String.class)
                .id(v -> KEYS[ArrayUtils.indexOf(OPTIONS, v)])
                .selfDisplay()
                .simpleConverter()
                .simpleProviderOf(OPTIONS);
    }

    private Component valueComponent() {
        return tester.getAssertionsForm().getSubComponentWithId("value_field").getTarget();
    }

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(STypeStringSelectItemAutoCompleteTest::buildBaseType);
    }

    @Test
    public void renderLabelsNotKeys() {
        tester.startDummyPage();

        Arrays.asList(OPTIONS).forEach(option -> tester.assertContains(option));
    }

    @Test
    public void renderField() {
        tester.startDummyPage();

        tester.getAssertionsForm().getSubComponents(TextField.class).isSize(2);
    }

    @Test
    public void submitsSelectedValueInsteadOfKey() {
        tester.startDummyPage();

        tester.newFormTester()
                .setValue(valueComponent(), KEYS[3])
                .submit();
        tester.getAssertionsForm().getSubComponentWithType(base).assertSInstance().isValueEquals(OPTIONS[3]);
    }

    @Test
    public void renderValueReadOnlyMode() {
        tester.getDummyPage().addInstancePopulator(instance->instance.getDescendant(base).setValue("Tony Stark"));
        tester.getDummyPage().setAsVisualizationView();
        tester.startDummyPage();

        assertThat(
                tester.getAssertionsForm().getSubComponentWithId("output").getTarget().getDefaultModelObjectAsString())
                .isEqualTo("Tony Stark");
    }

    @Test
    public void justIgnoresIfTheSelectedLabelHasNoMatch() {
        tester.startDummyPage();

        tester.newFormTester()
                .setValue(valueComponent(), "Tony Stark")
                .submit();
        tester.getAssertionsForm().getSubComponentWithType(base).assertSInstance().isValueNull();
    }

    @Test
    public void readOnlyKeyValueWithDynamicMode() {
        tester.getDummyPage().setTypeBuilder(tb->{
            buildBaseType(tb);
            base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        });
        tester.getDummyPage().addInstancePopulator(instance->instance.getDescendant(base).setValue("Clark Kent"));
        tester.getDummyPage().setAsVisualizationView();
        tester.startDummyPage();

        assertThat(
                tester.getAssertionsForm().getSubComponentWithId("output").getTarget().getDefaultModelObjectAsString())
                .isEqualTo("Clark Kent");
    }
}