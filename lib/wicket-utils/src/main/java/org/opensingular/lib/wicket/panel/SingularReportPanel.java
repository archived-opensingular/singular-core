package org.opensingular.lib.wicket.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputExcel;
import org.opensingular.lib.wicket.views.ViewOutputHtmlWebComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SingularReportPanel extends Panel {
    private final ISupplier<ViewGenerator> viewGeneratorSupplier;

    private IModel<String> titleModel = new Model<>();

    public SingularReportPanel(String id, ISupplier<ViewGenerator> viewGeneratorSupplier) {
        super(id);
        this.viewGeneratorSupplier = viewGeneratorSupplier;
        addTitle();
        addTable();
        addExportExcelLink();
    }

    private void addTable() {
        add(new ViewOutputHtmlWebComponent("table", viewGeneratorSupplier));
    }

    private void addTitle() {
        add(new Label("title", titleModel));
    }

    public SingularReportPanel setTitle(String title) {
        titleModel.setObject(title);
        return this;
    }

    public void addExportExcelLink() {
        DownloadLink downloadLink = new DownloadLink("excel", new Model<File>() {
            @Override
            public File getObject() {
                try {
                    ViewOutputExcel viewOutputExcel = new ViewOutputExcel(titleModel.getObject());
                    viewGeneratorSupplier.get().generateView(viewOutputExcel);
                    File xlsx = File.createTempFile("report", ".xlsx");
                    FileOutputStream fos = new FileOutputStream(xlsx);
                    viewOutputExcel.write(fos);
                    xlsx.deleteOnExit();
                    return xlsx;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, "report.xlsx");
        add(downloadLink);
    }
}
