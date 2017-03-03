package org.opensingular.form.wicket.test.base;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularWicketTester;

public class DummyPageTest {

    @Test
    public void testPageRendering() {
        WicketTester tester = new SingularWicketTester();

        DummyPage dummyPage = new DummyPage();
        dummyPage.setTypeBuilder((x) -> {x.addFieldString("mockString");});

        dummyPage.setInstanceCreator( (refType) -> {
            SDocumentFactory factory = dummyPage.mockFormConfig.getDocumentFactory();
            return (SIComposite) factory.createInstance(refType);
        });
        tester.startPage(dummyPage);
        tester.assertRenderedPage(DummyPage.class);
    }
}
