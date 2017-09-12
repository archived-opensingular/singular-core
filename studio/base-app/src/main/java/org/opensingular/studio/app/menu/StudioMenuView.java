package org.opensingular.studio.app.menu;

import org.opensingular.studio.app.definition.StudioDefinition;
import org.opensingular.studio.app.util.StudioWicketUtils;
import org.opensingular.studio.app.wicket.pages.StudioCRUDPage;
import org.opensingular.studio.core.menu.MenuView;

public class StudioMenuView implements MenuView {
    private final StudioDefinition studioDefinition;

    public StudioMenuView(StudioDefinition studioDefinition) {
        this.studioDefinition = studioDefinition;
    }

    @Override
    public String getEndpoint(String menuPath) {
        return StudioWicketUtils.getMergedPathIntoURL(StudioCRUDPage.class, menuPath);
    }

    public StudioDefinition getStudioDefinition() {
        return studioDefinition;
    }
}