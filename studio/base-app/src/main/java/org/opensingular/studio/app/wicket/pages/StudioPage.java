package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.studio.app.menu.StudioMenuItem;

public class StudioPage extends StudioTemplate {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = new Form<>("form");
        form.setMultiPart(true);
        StudioMenuItem studioMenuItem = findCurrentStudioMenuItem();
        if (studioMenuItem == null) {
            form.add(new WebMarkupContainer("crud"));
        } else {
            String beanName = studioMenuItem.getRepositoryBeanName();
            form.add(new SingularStudioSimpleCRUDPanel<STypeComposite<SIComposite>, SIComposite>("crud", () -> (FormRespository) ApplicationContextProvider.get().getBean(beanName)) {
                @Override
                protected void buildListTable(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder) {
                    findCurrentStudioMenuItem().configureTable(dataTableBuilder);
                }
            }.setCrudTitle(studioMenuItem.getName()));
        }
        add(form);
    }

}