package br.net.mirante.singular.form.wicket.validation;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;
import org.junit.Assert;
import org.junit.Test;

public class SimpleVisibilityValidationTest extends SingularFormBaseTest {

    STypeString fieldOne;
    STypeString fieldTwo;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        fieldOne = mockType.addFieldString("fieldOne");
        fieldOne.asAtr().required(true);

        fieldTwo = mockType.addFieldString("fieldTwo");
        fieldTwo.asAtr().visible(i -> false);
        fieldTwo.asAtr().required(true);
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        form.submit(page.getSingularValidationButton());
        Assert.assertFalse(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertTrue(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }
}
