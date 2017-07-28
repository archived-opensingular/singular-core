package org.opensingular.lib.wicket.views;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class SingularReportPanelTest {
    WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = new WicketTester();
    }

    @Test
    public void testRendering() throws Exception {
        tester.startPage(new MockSingularReportPage(id -> new SingularReportPanel<ReportMetadata<Void>, Void>(id, () -> null) {
            @Override
            protected void customizeModal(BSModalBorder searchModal) {

            }

            @Override
            protected ReportMetadata<Void> getReportMetadata() {
                return null;
            }
        }));
        tester.assertRenderedPage(MockSingularReportPage.class);
    }

    @Test
    public void testRenderingSearchModal() throws Exception {
        BSModalBorder filter = new BSModalBorder("modal");
        tester.startPage(new MockSingularReportPage(id -> new SingularReportPanel<ReportMetadata<Void>, Void>(id, () -> null) {
            @Override
            protected void customizeModal(BSModalBorder searchModal) {

            }

            @Override
            protected ReportMetadata<Void> getReportMetadata() {
                return null;
            }
        }));
        tester.executeAjaxEvent("f:srp:form:search", "click");
        tester.assertVisible(filter.getPageRelativePath());
    }

}