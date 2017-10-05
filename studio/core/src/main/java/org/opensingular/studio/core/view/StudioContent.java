package org.opensingular.studio.core.view;

import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.studio.core.menu.MenuEntry;

public abstract class StudioContent extends Panel {

    private MenuEntry currentMenuEntry;

    public StudioContent(String id, MenuEntry currentMenuEntry) {
        super(id);
        this.currentMenuEntry= currentMenuEntry;
    }

    public MenuEntry getCurrentMenuEntry() {
        return currentMenuEntry;
    }
}