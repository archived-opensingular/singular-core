package org.opensingular.studio.core.menu;

import org.opensingular.studio.core.util.StudioWicketUtils;
import org.opensingular.studio.core.view.StudioCRUDPage;

public class SidebarMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        return StudioWicketUtils.getMergedPathIntoURL(StudioCRUDPage.class, menuPath);
    }
}