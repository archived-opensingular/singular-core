package org.opensingular.studio.app.menu;

import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.menu.AbstractMenuEntry;
import org.opensingular.studio.core.menu.MenuView;

public class ItemMenuEntry extends AbstractMenuEntry {
    public ItemMenuEntry(Icon icon, String name, MenuView view) {
        super(icon, name, view);
    }

    public ItemMenuEntry(String name, MenuView view) {
        super(null, name, view);
    }
}