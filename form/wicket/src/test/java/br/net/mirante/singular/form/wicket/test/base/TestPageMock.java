package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class TestPageMock {

    @Test
    public void testPageRendering() {
        WicketTester tester = new WicketTester();

        MockPage mockPage = new MockPage() ;
        mockPage.setTypeBuilder((x) -> {x.addFieldString("mockString");});

        mockPage.setInstanceCreator( (x) -> {
            SDocumentFactory factory = mockPage.mockFormConfig.getDocumentFactory();
            RefType refType = new RefType() { protected SType<?> retrieve() { return x; } };
            return (SIComposite) factory.createInstance(refType);
        });
        tester.startPage(mockPage);
        tester.assertRenderedPage(MockPage.class);
    }
}
