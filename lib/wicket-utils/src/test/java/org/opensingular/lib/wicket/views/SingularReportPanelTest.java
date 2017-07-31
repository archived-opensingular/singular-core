package org.opensingular.lib.wicket.views;

import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class SingularReportPanelTest extends WicketTestCase {
    @Test
    public void testRendering() throws Exception {
        tester.startPage(new MockSingularReportPage(id -> new BlankSingularReportPanel(id, () -> null)));
        tester.assertRenderedPage(MockSingularReportPage.class);
    }

    @Test
    public void testRenderingSearchModal() throws Exception {
        BSModalBorder filter = new BSModalBorder("modal");
        tester.startPage(new MockSingularReportPage(id -> new BlankSingularReportPanel(id, () -> null)));
        tester.executeAjaxEvent("f:srp:form:search", "click");
        tester.assertVisible(filter.getPageRelativePath());
    }
}