package org.opensingular.form.wicket.validation;

import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

@Ignore
public class SimpleVisibilityValidationTest {

    STypeString fieldOne;
    STypeString fieldTwo;

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {

        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(baseType -> {
            fieldOne = baseType.addFieldString("fieldOne");
            fieldTwo = baseType.addFieldString("fieldTwo");
            fieldOne.asAtr().required(true);
            fieldTwo.asAtr().required(true).visible(i -> false);
        });

        ctx.startDummyPage();
        ctx.newFormTester("form").submit(ctx.getDummyPage().getSingularValidationButton());

        ctx.getAssertionsForm().getSubCompomentWithType(fieldOne).assertSInstance()
                .assertThatValidationErrors()
                .hasSize(1);

        ctx.getAssertionsForm().getSubCompomentWithType(fieldTwo).assertSInstance()
                .assertThatValidationErrors()
                .isEmpty();

    }

}