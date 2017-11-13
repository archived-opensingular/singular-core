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

package org.opensingular.form.wicket.validation;

import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.assertj.core.api.IterableAssert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.ValidationError;
import org.opensingular.form.wicket.helpers.SingularFormDummyPageTester;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;

public class DinamicVisiblityValidationTest {

    private static final String testValue = "fvrw1e4r5t4e.r6";
    private static final String FIELD_ONE = "fieldOne";
    private static final String FIELD_TWO = "fieldTwo";

    private SingularFormDummyPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularFormDummyPageTester();
        tester.getDummyPage().setTypeBuilder(root -> {
            STypeString fieldOne = root.addFieldString("fieldOne");
            STypeString fieldTwo = root.addFieldString("fieldTwo");

            fieldOne.asAtr()
                    .required(true);

            fieldTwo.asAtr()
                    .dependsOn(fieldOne)
                    .visible(instance -> instance.findNearestValue(fieldOne, String.class).orElse("").equals(testValue))
                    .required(true);
        });
        tester.startDummyPage();
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        submitValidationButton();
        asserThatValidationErrorsOfFieldOne().hasSize(1);
        asserThatValidationErrorsOfFieldTwo().isEmpty();
    }

    @Test
    public void testIfNotContaisErrorForFieldTwoAfterChangeFieldOneValueWhithWrongValue() {
        setValueOnFieldOneAndCallAjaxValidate("abas" + testValue + "2132");
        submitValidationButton();
        asserThatValidationErrorsOfFieldOne().isEmpty();
        asserThatValidationErrorsOfFieldTwo().isEmpty();

    }

    @Test
    public void testIfContaisErrorForFieldTwoAfterChangeFieldOneValue() {
        setValueOnFieldOneAndCallAjaxValidate(testValue);
        submitValidationButton();
        asserThatValidationErrorsOfFieldOne().isEmpty();
        asserThatValidationErrorsOfFieldTwo().hasSize(1);

    }

    private void setValueOnFieldOneAndCallAjaxValidate(String value) {
        FormTester formTester = tester.newFormTester();
        Component  fieldOne   = tester.getAssertionsForm().getSubComponentWithId(FIELD_ONE).getTarget();
        formTester.setValue(fieldOne, value);
        callAjaxProcessEvent(fieldOne);
    }

    private void submitValidationButton() {
        tester.newFormTester().submit(tester.getDummyPage().getSingularValidationButton());
    }

    private IterableAssert<ValidationError> asserThatValidationErrorsOfFieldTwo() {
        return tester.getAssertionsForm()
                .getSubComponentWithType(
                        tester.findTypeBySimpleName(FIELD_TWO).isInstanceOf(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }

    private IterableAssert<ValidationError> asserThatValidationErrorsOfFieldOne() {
        return tester.getAssertionsForm()
                .getSubComponentWithType(
                        tester.findTypeBySimpleName(FIELD_ONE).isInstanceOf(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }

    private void callAjaxProcessEvent(Component fieldOne) {
        tester.executeAjaxEvent(fieldOne, SINGULAR_PROCESS_EVENT);
    }

}