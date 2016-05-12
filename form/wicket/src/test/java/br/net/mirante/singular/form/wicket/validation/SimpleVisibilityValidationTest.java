package br.net.mirante.singular.form.wicket.validation;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;

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
        Assert.assertTrue(findModelsByType(fieldOne).findFirst().get().getMInstancia().hasValidationErrors());
        Assert.assertFalse(findModelsByType(fieldTwo).findFirst().get().getMInstancia().hasValidationErrors());
    }
}
