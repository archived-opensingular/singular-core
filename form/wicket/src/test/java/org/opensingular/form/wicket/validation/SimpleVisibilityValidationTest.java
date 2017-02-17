package org.opensingular.form.wicket.validation;

import org.fest.assertions.api.IterableAssert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

@Ignore
public class SimpleVisibilityValidationTest {

    private static final String FIELD_ONE = "fieldOne";
    private static final String FIELD_TWO = "fieldTwo";

    private SingularDummyFormPageTester tester;

    @Before
    public void setUp(){
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(baseType -> {

            STypeString fieldOne = baseType.addFieldString(FIELD_ONE);
            STypeString fieldTwo = baseType.addFieldString(FIELD_TWO);

            fieldOne.asAtr().required(true);
            fieldTwo.asAtr().required(true).visible(i -> false);

        });
        tester.startDummyPage();
        tester.newFormTester().submit(tester.getDummyPage().getSingularValidationButton());
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        assertThatFieldValidationErros(FIELD_ONE).hasSize(1);
        assertThatFieldValidationErros(FIELD_TWO).isEmpty();
    }

    private IterableAssert<IValidationError> assertThatFieldValidationErros(String field) {
        return tester.getAssertionsForm()
                .getSubCompomentWithType(
                        tester.findTypeBySimpleName(field).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors();
    }


}