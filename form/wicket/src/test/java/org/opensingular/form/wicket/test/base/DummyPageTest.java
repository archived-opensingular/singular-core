package org.opensingular.form.wicket.test.base;

import org.junit.Test;
import org.opensingular.form.SIComposite;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.form.wicket.helpers.SingularWicketTester;

public class DummyPageTest {

    @Test
    public void testPageRendering() {
        SingularWicketTester tester = new SingularWicketTester();

        DummyPage dummyPage = new DummyPage();
        dummyPage.setTypeBuilder((x) -> {x.addFieldString("mockString");});

        dummyPage.setInstanceCreator( (refType) -> {
            SDocumentFactory factory = dummyPage.mockFormConfig.getDocumentFactory();
            return (SIComposite) factory.createInstance(refType);
        });
        tester.startPage(dummyPage);
        tester.assertRenderedPage(DummyPage.class);

        tester.getAssertionsForm().isNotNull();
        tester.getAssertionsForm().getSubCompomentWithId("mockString");
        tester.getAssertionsForSubComp("mockString").isNotNull();
    }
}
