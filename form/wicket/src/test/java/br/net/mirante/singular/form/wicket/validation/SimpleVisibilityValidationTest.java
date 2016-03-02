package br.net.mirante.singular.form.wicket.validation;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.base.AbstractSingularFormTest;

public class SimpleVisibilityValidationTest extends AbstractSingularFormTest {

    STypeString fieldOne;
    STypeString fieldTwo;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        fieldOne = mockType.addCampoString("fieldOne");
        fieldOne.asAtrCore().obrigatorio(true);

        fieldTwo = mockType.addCampoString("fieldTwo");
        fieldTwo.asAtrBasic().visivel(i -> false);
        fieldTwo.asAtrCore().obrigatorio(true);
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        formTester.submit(mockPage.getSingularValidationButton());
        Assert.assertFalse(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertTrue(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }
}
