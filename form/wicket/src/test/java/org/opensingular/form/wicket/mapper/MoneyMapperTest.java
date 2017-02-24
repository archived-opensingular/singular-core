package org.opensingular.form.wicket.mapper;

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Test;
import org.opensingular.form.wicket.helpers.AssertionsWTextField;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

public class MoneyMapperTest {

    @Test
    public void testEditRendering() {
        SingularDummyFormPageTester ctx = new SingularDummyFormPageTester();
        ctx.getDummyPage().setTypeBuilder(baseType ->  baseType.addFieldMonetary("money"));
        ctx.getDummyPage().addInstancePopulator(instance -> instance.setValue("money", "10,00"));
        ctx.getDummyPage().setAsEditView();
        ctx.startDummyPage();

        ctx.getAssertionsForm().getSubComponents(TextField.class).isSize(1).get(0);
        AssertionsWTextField textField = ctx.getAssertionsForm().getSubCompomentWithId("money").asTextField();
        textField.assertValue().isEqualTo("10,00");
    }

}