package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public interface MenuEntry {
    Icon getIcon();

    String getName();

    MenuEntry getParent();

    void setParent(MenuEntry parent);
}