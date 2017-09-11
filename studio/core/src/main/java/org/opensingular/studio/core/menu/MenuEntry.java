package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public interface MenuEntry {

    /**
     * @return The element icon
     */
    Icon getIcon();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the parent
     */
    MenuEntry getParent();

    /**
     * Set the parent of the entry
     * @param parent
     */
    void setParent(MenuEntry parent);
}