package br.net.mirante.singular.form.wicket.validation;

import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.helpers.SingularFormBaseTest;

public class DinamicVisiblityValidationTest extends SingularFormBaseTest {

    String testValue = "fvrw1e4r5t4e.r6";
    STypeString fieldOne;
    STypeString fieldTwo;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {
        fieldOne = mockType.addFieldString("fieldOne");
        fieldOne.asAtrCore().required(true);
        fieldTwo = mockType.addFieldString("fieldTwo");

        fieldTwo.asAtrBasic().dependsOn(fieldOne);
        fieldTwo.asAtrBasic()
                .visivel(instance -> instance.findNearestValue(fieldOne, String.class).orElse("").equals(testValue));
        fieldTwo.asAtrCore().required(true);
    }

    @Test
    public void testIfContaisErrorOnlyForFieldOne() {
        form.submit(page.getSingularValidationButton());
        Assert.assertFalse(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertTrue(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }

    @Test
    public void testIfNotContaisErrorForFieldTwoAfterChangeFieldOneValueWhithWrongValue() {
        form.setValue(findFieldOneFormComponent(), "abas" + testValue + "2132");
        tester.executeAjaxEvent(findFieldOneFormComponent(), "change");
        form.submit(page.getSingularValidationButton());
        Assert.assertTrue(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertTrue(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }

    @Test
    public void testIfContaisErrorForFieldTwoAfterChangeFieldOneValue() {
        form.setValue(findFieldOneFormComponent(), testValue);
        tester.executeAjaxEvent(findFieldOneFormComponent(), "change");
        form.submit(page.getSingularValidationButton());
        Assert.assertTrue(findFormComponentsByType(fieldOne).findFirst().get().getFeedbackMessages().isEmpty());
        Assert.assertFalse(findFormComponentsByType(fieldTwo).findFirst().get().getFeedbackMessages().isEmpty());
    }

    public FormComponent findFieldOneFormComponent() {
        return findFormComponentsByType(fieldOne).findFirst().get();
    }
}
