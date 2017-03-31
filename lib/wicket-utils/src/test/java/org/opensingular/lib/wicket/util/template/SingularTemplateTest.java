package org.opensingular.lib.wicket.util.template;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;

public class SingularTemplateTest {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());
        tester.startPage(new SingularTemplate() {
        });
        
        tester.assertNoErrorMessage();
    }

}
