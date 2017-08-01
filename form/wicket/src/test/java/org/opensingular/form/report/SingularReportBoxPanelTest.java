package org.opensingular.form.report;

import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.Test;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.table.ColumnType;
import org.opensingular.lib.commons.table.TableTool;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

public class SingularReportBoxPanelTest extends WicketTestCase {

    @Test
    public void testRendering() throws Exception {
        SingularReportBoxPanel reportBoxPanel = new SingularReportBoxPanel("box") {
            @Override
            protected void configureMenu(MetronicMenu menu) {
                menu.addItem(new ReportAjaxMenuItem(DefaultIcons.PENCIL, "X", makeSingularReport()));
            }
        };
        tester.startComponentInPage(reportBoxPanel);
        tester.assertComponent("box", SingularReportBoxPanel.class);
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
            public ViewGenerator makeViewGenerator(ReportMetadata reportMetadata) {
                TableTool tableTool = new TableTool();
                tableTool.addColumn(ColumnType.STRING, "nome");
                tableTool.createSimpleTablePopulator()
                        .insertLine()
                        .setValor(0, "Danilo");
                return tableTool;
            }
        };
    }


}