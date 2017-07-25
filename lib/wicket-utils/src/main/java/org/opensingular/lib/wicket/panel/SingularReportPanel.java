package org.opensingular.lib.wicket.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.wicket.views.ViewOutputHtmlWebComponent;

public class SingularReportPanel extends Panel {
    private final ISupplier<ViewGenerator> viewGeneratorSupplier;

    private IModel<String> titleModel = new Model<>();

    public SingularReportPanel(String id, ISupplier<ViewGenerator> viewGeneratorSupplier) {
        super(id);
        this.viewGeneratorSupplier = viewGeneratorSupplier;
        addTitle();
        addTable();
//        viewGeneratorSupplier.get().generateView();
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
}
