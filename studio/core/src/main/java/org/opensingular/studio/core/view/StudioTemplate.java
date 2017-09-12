package org.opensingular.studio.core.view;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioMenu;

import javax.inject.Inject;
import java.util.List;

public abstract class StudioTemplate extends SingularAdminTemplate {
    @Inject
    private StudioMenu studioMenu;

    private String menuPath;

    private void resolveMenuPath() {
        StringValue pathStringValue = getPageParameters().get("path");
        if (pathStringValue.isNull() || pathStringValue.isEmpty()) {
            menuPath = "";
        } else {
            StringBuilder path = new StringBuilder(pathStringValue.toString());
            for (int i = 0; i < getPageParameters().getIndexedCount(); i++) {
                path.append('/').append(getPageParameters().get(i));
            }
            menuPath = path.toString();
        }
    }

    protected MenuEntry findCurrentMenuEntry() {
        return findCurrentMenuEntry(getStudioMenu().getChildren());
    }

    private MenuEntry findCurrentMenuEntry(List<MenuEntry> entries) {
        if (StringUtils.isBlank(getMenuPath())) {
            return null;
        }
        for (MenuEntry entry : entries) {
            if (entry.getMenuPath().endsWith(getMenuPath())) {
                return entry;
            }
            if (entry instanceof GroupMenuEntry) {
                MenuEntry foundedEntry = findCurrentMenuEntry(((GroupMenuEntry) entry).getChildren());
                if (foundedEntry != null) {
                    return foundedEntry;
                }
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

    protected String getMenuPath() {
        if (menuPath == null) {
            resolveMenuPath();
        }
        return menuPath;
    }

    protected static class StudioTemplateException extends RuntimeException {
        public StudioTemplateException(String s) {
            super(s);
        }
    }

    protected StudioMenu getStudioMenu() {
        return studioMenu;
    }
}