package org.opensingular.studio.core.menu;

import org.opensingular.studio.core.util.StudioWicketUtils;
import org.opensingular.studio.core.view.StudioPortalPage;

public class PortalMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        return StudioWicketUtils.getMergedPathIntoURL(StudioPortalPage.class, menuPath);
    }
}