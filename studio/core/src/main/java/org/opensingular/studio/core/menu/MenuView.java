package org.opensingular.studio.core.menu;

import org.opensingular.studio.core.view.StudioEmptyContent;
import org.opensingular.studio.core.view.StudioContent;
import org.opensingular.studio.core.view.StudioPortalContent;

public enum MenuView {
    PORTAL {
        @Override
        public StudioContent makeStudioContent(String id, MenuEntry entry) {
            return new StudioPortalContent(id, entry);
        }
    },
    SIDEBAR {
        @Override
        public StudioContent makeStudioContent(String id, MenuEntry entry) {
            return new StudioEmptyContent(id, entry);
        }
    };

    public abstract StudioContent makeStudioContent(String id, MenuEntry entry);

}