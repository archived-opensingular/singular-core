package org.opensingular.studio.app.menu;

import org.opensingular.studio.app.util.StudioWicketUtils;
import org.opensingular.studio.app.wicket.pages.StudioCRUDPage;
import org.opensingular.studio.core.menu.MenuView;

public class SidebarMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        return StudioWicketUtils.getMergedPathIntoURL(StudioCRUDPage.class, menuPath);
    }
}