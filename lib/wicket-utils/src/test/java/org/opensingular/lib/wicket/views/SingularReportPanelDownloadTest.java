package org.opensingular.lib.wicket.views;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.util.tester.WicketTestCase;
import org.apache.wicket.util.visit.IVisitor;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.report.ReportFilter;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.TablePopulator;
import org.opensingular.lib.commons.table.TableTool;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;

import java.io.IOException;

public class SingularReportPanelDownloadTest extends WicketTestCase {
    private static final boolean OPEN_DOWNLOADED_FILE = false;

    private SingularReport<ReportMetadata<ReportFilter>, ReportFilter> singularReport;
    private MockSingularReportPage mockSingularReportPage;

    @Before
    public void setUp() throws Exception {
        singularReport = makeSingularReport();
        mockSingularReportPage = new MockSingularReportPage(id -> new BlankSingularReportPanel(id, () -> singularReport));
        tester.startPage(mockSingularReportPage);
    }

    @Test
    public void testDownloadExcel() throws Exception {
        testDownloadFor(ViewOutputFormat.EXCEL);
    }

    @Test
    public void testDownloadHTML() throws Exception {
        testDownloadFor(ViewOutputFormat.HTML);
    }

    private void testDownloadFor(ViewOutputFormatExportable format) throws IOException {
        DownloadLink exportDownloadLink = getDownloadLinkByFormat(format);
        tester.clickLink(exportDownloadLink);
        assertTrue(tester.getContentLengthFromResponseHeader() > 0);
        if (OPEN_DOWNLOADED_FILE) {
            SingularTestUtil.showFileOnDesktopForUserAndWaitOpening(this, format.getFileExtension(), out -> {
                out.write(tester.getLastResponse().getBinaryContent());
            });
        }
    }

    private DownloadLink getDownloadLinkByFormat(ViewOutputFormatExportable format) {
        return ((MarkupContainer) mockSingularReportPage.get("f:srp:form:export-list:export-list-item"))
                .visitChildren(DownloadLink.class, (IVisitor<DownloadLink, DownloadLink>) (downloadLink, visit) -> {
                    if (format.getName().equalsIgnoreCase(downloadLink.get("export-label").getDefaultModelObjectAsString())) {
                        visit.stop(downloadLink);
                    }
                });
    }

    @NotNull
    private SingularReport<ReportMetadata<ReportFilter>, ReportFilter> makeSingularReport() {
        return new SingularReport<ReportMetadata<ReportFilter>, ReportFilter>() {
            @Override
            public String getReportName() {
                return "X";
            }

            @Override
            public ViewGenerator makeViewGenerator(ReportMetadata<ReportFilter> reportMetadata) {
                TableTool tt = new TableTool();
                tt.addColumn(ColumnType.STRING, "nome");
                tt.addColumn(ColumnType.INTEGER, "idade");
                TablePopulator populator = tt.createSimpleTablePopulator();
                populator.insertLine();
                populator.setValor(0, "John");
                populator.setValor(1, 25);
                return tt;
            }

            @Override
            public Boolean eagerLoading() {
                return Boolean.TRUE;
            }
        };
    }
}
