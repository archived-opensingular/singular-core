package org.opensingular.form.wicket.validation;

import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.fest.assertions.api.IterableAssert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class DinamicVisiblityValidationTest {

    private static final String testValue = "fvrw1e4r5t4e.r6";
    private static final String FIELD_ONE = "fieldOne";
    private static final String FIELD_TWO = "fieldTwo";

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp() {
        tester = new SingularDummyFormPageTester();
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
        Component  fieldOne   = tester.getAssertionsForm().getSubCompomentWithId(FIELD_ONE).getTarget();
        formTester.setValue(fieldOne, value);
        callAjaxProcessEvent(fieldOne);
    }

    private void submitValidationButton() {
        tester.newFormTester().submit(tester.getDummyPage().getSingularValidationButton());
    }

    private IterableAssert<IValidationError> asserThatValidationErrorsOfFieldTwo() {
        return tester.getAssertionsForm()
                .getSubCompomentWithType(
                        tester.findTypeBySimpleName(FIELD_TWO).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }

    private IterableAssert<IValidationError> asserThatValidationErrorsOfFieldOne() {
        return tester.getAssertionsForm()
                .getSubCompomentWithType(
                        tester.findTypeBySimpleName(FIELD_ONE).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }

    private void callAjaxProcessEvent(Component fieldOne) {
        tester.executeAjaxEvent(fieldOne, IWicketComponentMapper.SINGULAR_PROCESS_EVENT);
    }

}