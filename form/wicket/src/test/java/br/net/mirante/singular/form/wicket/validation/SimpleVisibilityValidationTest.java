package br.net.mirante.singular.form.wicket.validation;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;

public class SimpleVisibilityValidationTest extends AbstractSingularFormTest {

    STypeString fieldOne;
    STypeString fieldTwo;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

        fieldOne = mockType.addFieldString("fieldOne");
        fieldOne.asAtrCore().obrigatorio(true);

        fieldTwo = mockType.addFieldString("fieldTwo");
        fieldTwo.asAtrBasic().visivel(i -> false);
        fieldTwo.asAtrCore().obrigatorio(true);
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        form.submit(page.getSingularValidationButton());
        Assert.assertFalse(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertTrue(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }
}
