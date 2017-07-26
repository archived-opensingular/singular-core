package org.opensingular.lib.wicket.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputExcel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.views.WicketViewWrapperForViewOutputHtml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SingularReportPanel extends Panel {
    private final ISupplier<ViewGenerator> viewGeneratorSupplier;
    private final BSModalBorder filterModalBorder;

    private IModel<String> titleModel = new Model<>();

    public SingularReportPanel(String id, ISupplier<ViewGenerator> viewGeneratorSupplier, BSModalBorder filterModalBorder) {
        super(id);
        this.viewGeneratorSupplier = viewGeneratorSupplier;
        this.filterModalBorder = filterModalBorder;
        addTitle();
        addTable();
        addExportExcelLink();
        addSearch();
    }

    private void addSearch() {
        AjaxButton ajaxButton = new AjaxButton("search") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (filterModalBorder != null) {
                    filterModalBorder.show(target);
                }
            }
        };
        add(ajaxButton);
        ajaxButton.setVisible(filterModalBorder != null);
    }

    private void addTable() {
        add(new WicketViewWrapperForViewOutputHtml("table", viewGeneratorSupplier));
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
