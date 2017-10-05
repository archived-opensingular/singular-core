package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.view.StudioCRUDContent;
import org.opensingular.studio.core.view.StudioContent;

public class StudioCRUDMenuEntry extends ItemMenuEntry {

    private final StudioDefinition studioDefinition;

    public StudioCRUDMenuEntry(Icon icon, String name, StudioDefinition studioDefinition) {
        super(icon, name);
        this.studioDefinition = studioDefinition;
    }

    public StudioDefinition getStudioDefinition() {
        return studioDefinition;
    }

    @Override
    public StudioContent makeContent(String id) {
        return new StudioCRUDContent(id, this);
    }
}
