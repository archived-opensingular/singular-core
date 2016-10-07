package org.opensingular.form.wicket.test.base;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.helpers.DummyPage;
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
