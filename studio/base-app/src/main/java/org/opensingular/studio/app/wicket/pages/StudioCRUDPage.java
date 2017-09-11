package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.SingularStudioSimpleCRUDPanel;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.menu.AbstractMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.util.Shortcuts;
import org.opensingular.studio.app.definition.StudioDefinition;
import org.opensingular.studio.app.menu.GroupMenuEntry;
import org.opensingular.studio.app.menu.ItemMenuEntry;
import org.opensingular.studio.app.menu.StudioMenuView;
import org.opensingular.studio.core.menu.MenuEntry;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/studio/${path}")
public class StudioCRUDPage extends StudioTemplate implements Loggable {
    private StudioDefinition definition;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = new Form<>("form");
        form.setMultiPart(true);
        MenuEntry entry = findCurrentMenuEntry();
        if (isStudioItem(entry)) {
            addCrudContent(form, entry);
        } else {
            addEmptyContent(form);
        }
        add(form);
    }

    private void addCrudContent(Form<Void> form, MenuEntry entry) {
        StudioMenuView view = (StudioMenuView) entry.getView();
        definition = view.getStudioDefinition();
        String beanName = definition.getRepositoryBeanName();
        if (ApplicationContextProvider.get().containsBean(beanName)) {
            form.add(new SingularStudioSimpleCRUDPanel<STypeComposite<SIComposite>, SIComposite>("crud"
                    , () -> (FormRespository) ApplicationContextProvider.get().getBean(beanName)
                    , definition::getPermissionStrategy) {
                @Override
                protected void buildListTable(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder) {
                    definition.configureDatatableColumns(dataTableBuilder);
                }
            }.setCrudTitle(definition.getTitle()));
        } else {
            getLogger().warn("Não foi encontrado o bean {}", beanName);
            addEmptyContent(form);
        }
    }

    private boolean isStudioItem(MenuEntry entry) {
        return entry != null && entry.getView() instanceof StudioMenuView;
    }

    private void addEmptyContent(Form<Void> form) {
        form.add(new WebMarkupContainer("crud"));
    }

    @NotNull
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

    public static String getPageEndpoint(String pathParameter) {
        String[] paths = pathParameter.split("/");
        StringBuilder path = new StringBuilder();
        if (paths.length > 0) {
            path.append((String) RequestCycle.get().urlFor(StudioCRUDPage.class, new PageParameters().add("path", paths[0])));
            for (int i = 1; i < paths.length; i++) {
                path.append('/').append(paths[i]);
            }
        }
        return WebApplication.get().getServletContext().getContextPath() + path.toString();
    }
}