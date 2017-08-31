package org.opensingular.form.studio;


import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.opensingular.form.spring.SpringFormPersistenceInMemory;
import org.opensingular.form.wicket.helpers.DummyPage;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;

public class SingularStudioSimpleCRUDTestPage extends WebPage {

    public SpringFormPersistenceInMemory springFormPersistenceInMemory;
    public SingularStudioSimpleCRUDPanel singularStudioSimpleCRUDPanel;
    public Form form = new Form("form");

    public SingularStudioSimpleCRUDTestPage() {
        springFormPersistenceInMemory = new SpringFormPersistenceInMemory(SingularStudioSimpleCRUDPanelTest.SimpleCrudType.class);
        springFormPersistenceInMemory.setDocumentFactory(new DummyPage.MockSDocumentFactory());
        add(form);
        form.add(singularStudioSimpleCRUDPanel = new SingularStudioSimpleCRUDPanel("crud", springFormPersistenceInMemory) {
            @Override
            protected void buildListTable(BSDataTableBuilder dataTableBuilder) {

            }
        });
    }
}
