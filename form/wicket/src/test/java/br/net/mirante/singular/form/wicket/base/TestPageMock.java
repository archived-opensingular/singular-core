package br.net.mirante.singular.form.wicket.base;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import br.net.mirante.singular.form.mform.STypeComposite;

public class TestPageMock {

    @Test
    public void testPageRendering() {
        WicketTester tester = new WicketTester();

        MockPage mockPage = new MockPage() {
            @Override
            protected void populateType(STypeComposite<?> mockType) {
                mockType.addCampoString("mockString");
            }
        };
        tester.startPage(mockPage);
        tester.assertRenderedPage(MockPage.class);
    }
}
