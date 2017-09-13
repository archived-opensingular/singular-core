package org.opensingular.studio.core.menu;

import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.util.StudioWicketUtils;
import org.opensingular.studio.core.view.StudioCRUDPage;

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