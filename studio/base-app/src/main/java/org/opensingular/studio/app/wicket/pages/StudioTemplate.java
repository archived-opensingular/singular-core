package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.wicket.util.menu.AbstractMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.studio.app.menu.StudioMenuItem;
import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioMenu;

import javax.inject.Inject;
import java.util.List;

public abstract class StudioTemplate extends SingularAdminTemplate {
    @Inject
    private StudioMenu studioMenu;

    private String menuPath;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        resolveMenuPath();
    }

    private void resolveMenuPath() {
        StringValue pathStringValue = getPageParameters().get("path");
        if (pathStringValue.isNull() || pathStringValue.isEmpty()) {
            menuPath = "";
        } else {
            StringBuilder path = new StringBuilder(pathStringValue.toString());
            for (int i = 0; i < getPageParameters().getIndexedCount(); i++) {
                path.append("/").append(getPageParameters().get(i));
            }
            menuPath = path.toString();
        }
    }

    protected StudioMenuItem findCurrentStudioMenuItem() {
        return findCurrentStudioMenuItem(studioMenu.getChildren());
    }

    private StudioMenuItem findCurrentStudioMenuItem(List<MenuEntry> entries) {
        for (MenuEntry entry : entries) {
            if (entry instanceof StudioMenuItem &&
                    ((StudioMenuItem) entry)
                            .getEndpoint()
                            .replace("/"+ StudioApplication.STUDIO_ROOT_PATH+"/", "")
                            .equals(menuPath)) {
                return (StudioMenuItem) entry;
            }
            if (entry instanceof GroupMenuEntry) {
                return findCurrentStudioMenuItem(((GroupMenuEntry) entry).getChildren());
            }
        }
        return null;
    }

    @Override
    protected IModel<String> getContentTitle() {
        return null;
    }

    @Override
    protected IModel<String> getContentSubtitle() {
        return null;
    }

    @Override
    protected boolean isWithMenu() {
        return true;
    }

    @NotNull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        MetronicMenu metronicMenu = new MetronicMenu(id);
        for (MenuEntry menuEntry : studioMenu.getChildren()) {
            metronicMenu.addItem(buildMenu(menuEntry));
        }
        return metronicMenu;
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
        throw new RuntimeException("O tipo de menu " + menuEntry.getClass().getName() + " não é suportado.");
    }

    public String getMenuPath() {
        return menuPath;
    }
}
