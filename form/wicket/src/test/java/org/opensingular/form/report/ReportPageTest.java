package org.opensingular.form.report;

import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.TableTool;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;

public class ReportPageTest extends WicketTestCase {

    @Test
    public void testRendering() throws Exception {
        ReportPage reportPage = new ReportPage(null) {
            @Override
            protected void configureMenu(ReportMenuBuilder menu) {
                menu.addItem(DefaultIcons.PENCIL, "X", () -> makeSingularReport());
            }
        };
        tester.startPage(reportPage);
        assertTrue(tester.getLastRenderedPage().equals(reportPage));
    }

    private SingularFormReport makeSingularReport() {
        return new SingularFormReport() {
            @Override
            public Class getFilterType() {
                return null;
            }

            @Override
            public String getReportName() {
                return "";
            }

            @Override
            public ViewGenerator makeViewGenerator(FormReportMetadata reportMetadata) {
                TableTool tableTool = new TableTool();
                tableTool.addColumn(ColumnType.STRING, "nome");
                tableTool.createSimpleTablePopulator()
                        .insertLine()
                        .setValue(0, "Danilo");
                return tableTool;
            }
        };
    }

    @Override
    protected WebApplication newApplication() {
        return new AdminApp();
    }

    private class AdminApp extends MockApplication implements SingularAdminApp {
    }

}