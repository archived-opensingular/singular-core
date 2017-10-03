package org.opensingular.studio.core.view;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.menu.AbstractMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.util.Shortcuts;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioMenuView;
import org.wicketstuff.annotation.mount.MountPath;

import javax.annotation.Nonnull;

@MountPath("/studio/${path}")
public class StudioCRUDPage extends StudioTemplate implements Loggable {
    private StudioDefinition definition;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = newStatelessIfEmptyForm();
        form.setMultiPart(true);
        MenuEntry entry = findCurrentMenuEntry();
        if (isStudioItem(entry)) {
            addCrudContent(form, entry);
        } else {
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

    private void addCrudContent(Form<Void> form, MenuEntry entry) {
        StudioMenuView view = (StudioMenuView) entry.getView();
        definition = view.getStudioDefinition();
        FormRespository respository = definition.getRepository();
        if (respository != null) {
            form.add(new SingularStudioSimpleCRUDPanel<STypeComposite<SIComposite>, SIComposite>("crud"
                    , definition::getRepository
                    , definition::getPermissionStrategy) {
                @Override
                protected void buildListTable(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder) {
                    StudioDefinition.StudioDataTable studioDataTable = new StudioDefinition.StudioDataTable();
                    definition.configureStudioDataTable(studioDataTable);
                    studioDataTable.getColumns().forEach((columnName, columnValuePath) -> {
                        dataTableBuilder.appendPropertyColumn(Model.of(columnName), ins -> ins.getValue(columnValuePath));
                    });
                }
            }.setCrudTitle(definition.getTitle()));
        } else {
            addEmptyContent(form);
        }
    }

    private boolean isStudioItem(MenuEntry entry) {
        return entry != null && entry.getView() instanceof StudioMenuView;
    }

    private void addEmptyContent(Form<Void> form) {
        form.add(new WebMarkupContainer("crud"));
    }

    @Nonnull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        MetronicMenu metronicMenu = new MetronicMenu(id);
        MenuEntry currentMenuEntry = findCurrentMenuEntry();
        if (currentMenuEntry != null) {
            while (currentMenuEntry instanceof ItemMenuEntry) {
                currentMenuEntry = currentMenuEntry.getParent();
            }
            AbstractMenuItem menu = buildMenu(currentMenuEntry);
            if (menu instanceof MetronicMenuGroup) {
                MetronicMenuGroup metronicMenuGroup = (MetronicMenuGroup) menu;
                metronicMenuGroup.setOpen();
            }
            metronicMenu.addItem(menu);
        }
        return metronicMenu;
    }

    @Override
    protected boolean isWithMenu() {
        return true;
    }

    private AbstractMenuItem buildMenu(MenuEntry menuEntry) {
        if (menuEntry instanceof GroupMenuEntry) {
            GroupMenuEntry group = (GroupMenuEntry) menuEntry;
            MetronicMenuGroup metronicMenuGroup = new MetronicMenuGroup(menuEntry.getIcon(), menuEntry.getName());
            for (MenuEntry child : group.getChildren()) {
                metronicMenuGroup.addItem(buildMenu(child));
            }
            return metronicMenuGroup;
        } else if (menuEntry instanceof ItemMenuEntry) {
            ItemMenuEntry item = (ItemMenuEntry) menuEntry;
            return new MetronicMenuItem(item.getIcon(), item.getName(), item.getEndpoint());
        }
        throw new StudioTemplateException("O tipo de menu " + menuEntry.getClass().getName() + " não é suportado.");

    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return Shortcuts.$m.get(() -> {
            if (definition != null) {
                return Model.of(definition.getTitle()).getObject();
            }
            return super.getPageTitleModel().getObject();
        });
    }

}