package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public abstract class AbstractMenuEntry implements MenuEntry {
    private Icon icon;
    private String name;
    private MenuEntry parent;

    public AbstractMenuEntry(Icon icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MenuEntry getParent() {
        return parent;
    }

    @Override
    public void setParent(MenuEntry parent) {
        this.parent = parent;
    }
}