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

package org.opensingular.form.wicket.mapper;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.junit.Test;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.wicket.helpers.AssertionsWComponent;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class BooleanMapperTest {

    private final static String ACEITA_TERMOS = "ACEITA_TERMOS";

    private SingularDummyFormPageTester createTesterWithSimpleCheckbox() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            root.addFieldBoolean(ACEITA_TERMOS).asAtr().label("Aceito os termos e condições");
        });
        return tester;
    }

    private SingularDummyFormPageTester createTesterWithSimpleRadio() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            STypeBoolean aceitaTermos = root.addFieldBoolean(ACEITA_TERMOS);
            aceitaTermos.asAtr().label("Aceito os termos e condições");
            aceitaTermos.withRadioView();
        });
        return tester;
    }

    private AssertionsSInstance assertWhenSubmitsThroughTheAceitaTermosComponent(String value) {
        SingularDummyFormPageTester tester = createTesterWithSimpleCheckboxAndStart();
        AssertionsWComponent aceitaTermos = tester.getAssertionsPage().getSubCompomentWithId(ACEITA_TERMOS);
        aceitaTermos.assertSInstance().isValueEquals(null);
        tester.newFormTester().setValue(aceitaTermos.getTarget(), value).submit();
        return aceitaTermos.assertSInstance();
    }

    private SingularDummyFormPageTester createTesterWithSimpleRadioAndStart() {
        return createTesterWithSimpleRadio().startDummyPage();
    }

    private SingularDummyFormPageTester createTesterWithSimpleCheckboxAndStart() {
        return createTesterWithSimpleCheckbox().startDummyPage();
    }

    @Test
    public void testIfSpecifiedLabelIsRendered() {
        createTesterWithSimpleCheckboxAndStart()
                .assertContains("Aceito os termos e condições");
    }

    @Test
    public void rendersACheckBoxByDefault() {
        createTesterWithSimpleCheckboxAndStart()
                .getAssertionsPage()
                .getSubComponents(CheckBox.class).isSize(1);
    }

    @Test
    public void rendersACheckBoxByDefaultUnckecked() {
        createTesterWithSimpleCheckboxAndStart()
                .getAssertionsPage()
                .getSubCompomentWithId(ACEITA_TERMOS).assertSInstance().isValueEquals(null);
    }

    @Test
    public void submitsFalseThroutghTheCheckbox() {
        assertWhenSubmitsThroughTheAceitaTermosComponent("false").isValueEquals(false);
    }

    @Test
    public void submitsTrueThroutghTheCheckbox() {
        assertWhenSubmitsThroughTheAceitaTermosComponent("true").isValueEquals(true);
    }

    @Test
    public void rendersACheckBoxCheckedWhenValueIsTrue() {
        SingularDummyFormPageTester tester = createTesterWithSimpleCheckbox();
        tester.getDummyPage().addInstancePopulator(root -> {
            root.setValue(ACEITA_TERMOS, true);
        });
        tester.startDummyPage();
        tester.getAssertionsPage().getSubCompomentWithId(ACEITA_TERMOS)
                .is(CheckBox.class)
                .assertSInstance().isValueEquals(true);
    }

    @Test
    public void rendersARadioChoiceIfAsked() {
        SingularDummyFormPageTester tester = createTesterWithSimpleRadioAndStart();
        List choices = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .isSize(1)
                .get(0)
                .getTarget(RadioChoice.class)
                .getChoices();
        assertThat(choices).containsOnly("Sim", "Não");
    }

    @Test
    public void rendersNoChoiceIfNoneIsSelected() {
        SingularDummyFormPageTester tester = createTesterWithSimpleRadioAndStart();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .isSize(1)
                .get(0)
                .getTarget(RadioChoice.class);
        assertThat(radioChoice.getDefaultModelObject()).isNull();
    }

    @Test
    public void submitsTheValueThroughTheRadioYes() {
        assertWhenSubmitsThroughTheAceitaTermosComponent("true").isValueEquals(true);
    }

    @Test
    public void submitsTheValueThroughTheRadioNo() {
        assertWhenSubmitsThroughTheAceitaTermosComponent("false").isValueEquals(false);
    }

    @Test
    public void rendersFalseChoiceIfFalseIsSelected() {
        SingularDummyFormPageTester tester = createTesterWithSimpleRadio();
        tester.getDummyPage().addInstancePopulator(si -> si.setValue(ACEITA_TERMOS, false));
        tester.startDummyPage();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .isSize(1)
                .get(0)
                .getTarget(RadioChoice.class);
        assertThat(radioChoice.getDefaultModelObject()).isEqualTo("Não");
    }

    @Test
    public void rendersTrueChoiceIfTrueIsSelected() {
        SingularDummyFormPageTester tester = createTesterWithSimpleRadio();
        tester.getDummyPage().addInstancePopulator(si -> si.setValue(ACEITA_TERMOS, true));
        tester.startDummyPage();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .isSize(1)
                .get(0)
                .getTarget(RadioChoice.class);
        assertThat(radioChoice.getDefaultModelObject()).isEqualTo("Sim");
    }

}