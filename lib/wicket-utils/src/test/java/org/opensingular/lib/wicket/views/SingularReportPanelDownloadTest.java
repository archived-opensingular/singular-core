/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.wicket.views;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.util.tester.WicketTestCase;
import org.apache.wicket.util.visit.IVisitor;
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

import javax.annotation.Nonnull;
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

    @Nonnull
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
                populator.setValue(0, "John");
                populator.setValue(1, 25);
                return tt;
            }

            @Override
            public Boolean eagerLoading() {
                return Boolean.TRUE;
            }
        };
    }
}
