package org.opensingular.form.wicket.validation;

import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

@Ignore
public class SimpleVisibilityValidationTest {

    private static final String FIELD_ONE = "fieldOne";
    private static final String FIELD_TWO = "fieldTwo";

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {

        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();

        tester.getDummyPage().setTypeBuilder(baseType -> {

            STypeString fieldOne = baseType.addFieldString(FIELD_ONE);
            STypeString fieldTwo = baseType.addFieldString(FIELD_TWO);

            fieldOne.asAtr().required(true);
            fieldTwo.asAtr().required(true).visible(i -> false);

        });

        tester.startDummyPage();

        tester.newFormTester().submit(tester.getDummyPage().getSingularValidationButton());

        tester.getAssertionsForm()
                .getSubCompomentWithType(
                        tester.findTypeBySimpleName(FIELD_ONE).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors()
                .hasSize(1);

        tester.getAssertionsForm()
                .getSubCompomentWithType(
                        tester.findTypeBySimpleName(FIELD_TWO).is(STypeString.class).getTarget()
                )
                .assertSInstance()
                .assertThatValidationErrors()
                .isEmpty();

    }


}