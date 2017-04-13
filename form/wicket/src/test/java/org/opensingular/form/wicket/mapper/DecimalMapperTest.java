package org.opensingular.form.wicket.mapper;

import org.junit.Test;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.math.BigDecimal;

public class DecimalMapperTest {

    @Test
    public void testIsRendering(){
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(tb-> tb.addFieldDecimal("decimal"));
        tester.getDummyPage().addInstancePopulator(instance -> instance.setValue("decimal", new BigDecimal(123.45)));

        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        BigDecimal expectedValue = new BigDecimal(123.45);
        tester.getAssertionsForm().getSubCompomentWithId("decimal").assertSInstance().isValueEquals(expectedValue);
    }
}
