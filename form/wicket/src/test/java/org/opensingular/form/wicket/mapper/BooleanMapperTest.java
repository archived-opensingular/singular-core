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
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanMapperTest {

    private final static String ACEITA_TERMOS = "ACEITA_TERMOS";

    private SingularFormDummyPageTester createTesterWithSimpleCheckbox() {
        SingularFormDummyPageTester tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            root.addFieldBoolean(ACEITA_TERMOS).asAtr().label("Aceito os termos e condições");
        });
        return tester;
    }

    private SingularFormDummyPageTester createTesterWithSimpleRadio() {
        SingularFormDummyPageTester tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            STypeBoolean aceitaTermos = root.addFieldBoolean(ACEITA_TERMOS);
            aceitaTermos.asAtr().label("Aceito os termos e condições");
            aceitaTermos.withRadioView();
        });
        return tester;
    }

    private AssertionsSInstance assertWhenSubmitsThroughTheAceitaTermosComponent(String value) {
        SingularFormDummyPageTester tester = createTesterWithSimpleCheckboxAndStart();
        AssertionsWComponent aceitaTermos = tester.getAssertionsPage().getSubComponentWithId(ACEITA_TERMOS);
        aceitaTermos.assertSInstance().isValueEquals(null);
        tester.newFormTester().setValue(aceitaTermos.getTarget(), value).submit();
        return aceitaTermos.assertSInstance();
    }

    private SingularFormDummyPageTester createTesterWithSimpleRadioAndStart() {
        return createTesterWithSimpleRadio().startDummyPage();
    }

    private SingularFormDummyPageTester createTesterWithSimpleCheckboxAndStart() {
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
                .getSubComponents(CheckBox.class).hasSize(1);
    }

    @Test
    public void rendersACheckBoxByDefaultUnckecked() {
        createTesterWithSimpleCheckboxAndStart()
                .getAssertionsPage()
                .getSubComponentWithId(ACEITA_TERMOS).assertSInstance().isValueEquals(null);
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
        SingularFormDummyPageTester tester = createTesterWithSimpleCheckbox();
        tester.getDummyPage().addInstancePopulator(root -> {
            root.setValue(ACEITA_TERMOS, true);
        });
        tester.startDummyPage();
        tester.getAssertionsPage().getSubComponentWithId(ACEITA_TERMOS)
                .isInstanceOf(CheckBox.class)
                .assertSInstance().isValueEquals(true);
    }

    @Test
    public void rendersARadioChoiceIfAsked() {
        SingularFormDummyPageTester tester = createTesterWithSimpleRadioAndStart();
        List choices = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .hasSize(1)
                .first()
                .getTarget(RadioChoice.class)
                .getChoices();
        assertThat(choices).containsOnly("Sim", "Não");
    }

    @Test
    public void rendersNoChoiceIfNoneIsSelected() {
        SingularFormDummyPageTester tester = createTesterWithSimpleRadioAndStart();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .hasSize(1)
                .first()
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
        SingularFormDummyPageTester tester = createTesterWithSimpleRadio();
        tester.getDummyPage().addInstancePopulator(si -> si.setValue(ACEITA_TERMOS, false));
        tester.startDummyPage();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .hasSize(1)
                .first()
                .getTarget(RadioChoice.class);
        assertThat(radioChoice.getDefaultModelObject()).isEqualTo("Não");
    }

    @Test
    public void rendersTrueChoiceIfTrueIsSelected() {
        SingularFormDummyPageTester tester = createTesterWithSimpleRadio();
        tester.getDummyPage().addInstancePopulator(si -> si.setValue(ACEITA_TERMOS, true));
        tester.startDummyPage();
        RadioChoice radioChoice = tester.getAssertionsPage().getSubComponents(RadioChoice.class)
                .hasSize(1)
                .first()
                .getTarget(RadioChoice.class);
        assertThat(radioChoice.getDefaultModelObject()).isEqualTo("Sim");
    }

}