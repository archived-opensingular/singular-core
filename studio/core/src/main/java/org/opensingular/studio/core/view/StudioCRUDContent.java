package org.opensingular.studio.core.view;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioCRUDMenuEntry;

import javax.annotation.Nonnull;


public class StudioCRUDContent extends StudioContent implements Loggable {

    private StudioDefinition definition;

    public StudioCRUDContent(String id, MenuEntry currentMenuEntry) {
        super(id, currentMenuEntry);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = newStatelessIfEmptyForm();
        form.setMultiPart(true);
        MenuEntry entry = getCurrentMenuEntry();
        if (isStudioItem(entry)) {
            addCrudContent(form, (StudioCRUDMenuEntry) entry);
        }
        else {
            addEmptyContent(form);
        }
        add(form);
    }

    @Nonnull
    private Form<Void> newStatelessIfEmptyForm() {
        return new Form<Void>("form") {
            @Override
            protected boolean getStatelessHint() {
                Component statefullComp = visitChildren(Component.class, (c, v) -> {
                    if (!c.isStateless()) {
                        v.stop(c);
                    }
                });
                return statefullComp == null;
            }
        };
    }

    private void addCrudContent(Form<Void> form, StudioCRUDMenuEntry entry) {
        definition = entry.getStudioDefinition();
        FormRespository respository = definition.getRepository();
        if (respository != null) {
            form.add(new SingularStudioSimpleCRUDPanel<STypeComposite<SIComposite>, SIComposite>("crud"
                    , definition::getRepository
                    , definition::getPermissionStrategy) {
                @Override
                protected void buildListTable(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder) {
                    StudioDefinition.StudioDataTable studioDataTable = new StudioDefinition.StudioDataTable();
                    definition.configureStudioDataTable(studioDataTable);
                    studioDataTable.getColumns().forEach((columnName, columnValuePath) -> dataTableBuilder.appendPropertyColumn(Model.of(columnName), ins -> ins.getValue(columnValuePath)));
                }
            }.setCrudTitle(definition.getTitle()));
        }
        else {
            addEmptyContent(form);
        }
    }

    private boolean isStudioItem(MenuEntry entry) {
        return entry instanceof StudioCRUDMenuEntry;
    }

    private void addEmptyContent(Form<Void> form) {
        form.add(new WebMarkupContainer("crud"));
    }
}