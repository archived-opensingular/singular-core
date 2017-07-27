package org.opensingular.lib.wicket.views;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

import java.util.function.Function;

public class MockSingularReportPage extends WebPage {

    public Form<?> form;
    public SingularReportPanel singularReportPanel;

    public MockSingularReportPage(Function<String, SingularReportPanel> singularReportPanelFactory) {
        addForm();
        addSingularReportPanel(singularReportPanelFactory);
    }

    private void addSingularReportPanel(Function<String, SingularReportPanel> singularReportPanelFactory) {
       singularReportPanel = singularReportPanelFactory.apply("srp");
       form.add(singularReportPanel);
    }

    private void addForm() {
        add(form = new Form<Void>("f"));
    }
}
