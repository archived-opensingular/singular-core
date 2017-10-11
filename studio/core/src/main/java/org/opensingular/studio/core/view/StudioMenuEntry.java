package org.opensingular.studio.core.view;

import org.opensingular.lib.commons.lambda.IBiFunction;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;

public class StudioMenuEntry extends ItemMenuEntry {

    private final IBiFunction<String, MenuEntry, StudioContent> contentFactory;
    private final boolean                                       withMenu;

    public StudioMenuEntry(Icon icon, String name, IBiFunction<String, MenuEntry, StudioContent> contentFactory,
                           boolean withMenu) {
        super(icon, name);
        this.contentFactory = contentFactory;
        this.withMenu = withMenu;
    }

    @Override
    public StudioContent makeContent(String id) {
        return contentFactory.apply(id, this);
    }

    @Override
    public boolean isWithMenu() {
        return withMenu;
    }
}
