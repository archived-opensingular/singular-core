package org.opensingular.lib.wicket.views;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.util.tester.WicketTestCase;
import org.apache.wicket.util.visit.IVisitor;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.PopulatorTable;
import org.opensingular.lib.commons.table.TableTool;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;
import org.opensingular.lib.commons.views.ViewOutputFormatExportable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SingualrReportPanelDownloadTest extends WicketTestCase {
    private static final boolean OPEN_DOWNLOADED_FILE = false;

    private SingularReport<ReportMetadata<Void>, Void> singularReport;
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
            Path path = Files.createTempFile("teste", "." + format.getFileExtension());
            File file = path.toFile();
            FileUtils.writeByteArrayToFile(file, tester.getLastResponse().getBinaryContent());
            SingularTestUtil.showFileOnDesktopForUserAndWaitOpening(file);
            file.deleteOnExit();
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
    private SingularReport<ReportMetadata<Void>, Void> makeSingularReport() {
        return new SingularReport<ReportMetadata<Void>, Void>() {
            @Override
            public String getReportName() {
                return "X";
            }

            @Override
            public ViewGenerator makeViewGenerator(ReportMetadata<Void> reportMetadata) {
                TableTool tt = new TableTool();
                tt.addColumn(ColumnType.STRING, "nome");
                tt.addColumn(ColumnType.INTEGER, "idade");
                PopulatorTable populator = tt.createSimpleTablePopulator();
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
