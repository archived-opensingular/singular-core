package org.opensingular.lib.wicket.views;

import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;

public class SingularReportPanelTest extends WicketTestCase {
    @Test
    public void testRendering() throws Exception {
        tester.startPage(new MockSingularReportPage(id -> new BlankSingularReportPanel(id, () -> null)));
        tester.assertRenderedPage(MockSingularReportPage.class);
    }
}