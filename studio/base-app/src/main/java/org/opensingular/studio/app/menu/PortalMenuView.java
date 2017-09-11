package org.opensingular.studio.app.menu;

import org.opensingular.studio.app.util.StudioWicketUtils;
import org.opensingular.studio.app.wicket.pages.StudioPortalPage;
import org.opensingular.studio.core.menu.MenuView;

public class PortalMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        return StudioWicketUtils.getMergedPathIntoURL(StudioPortalPage.class, menuPath);
    }
}