package br.net.mirante.singular.form.wicket.test.base;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.helpers.DummyPage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class DummyPageTest {

    @Test
    public void testPageRendering() {
        WicketTester tester = new WicketTester();
        tester.getApplication().getMarkupSettings().setDefaultMarkupEncoding("utf-8");

        DummyPage dummyPage = new DummyPage() ;
        dummyPage.setTypeBuilder((x) -> {x.addFieldString("mockString");});

        dummyPage.setInstanceCreator( (x) -> {
            SDocumentFactory factory = dummyPage.mockFormConfig.getDocumentFactory();
            RefType refType = new RefType() { protected SType<?> retrieve() { return x; } };
            return (SIComposite) factory.createInstance(refType);
        });
        tester.startPage(dummyPage);
        tester.assertRenderedPage(DummyPage.class);
    }
}
