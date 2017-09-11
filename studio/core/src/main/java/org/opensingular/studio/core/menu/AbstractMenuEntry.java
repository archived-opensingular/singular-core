package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

public abstract class AbstractMenuEntry implements MenuEntry {
    private Icon icon;
    private String name;
    private MenuEntry parent;
    private MenuView view;

    public AbstractMenuEntry(Icon icon, String name, MenuView view) {
        this.icon = icon;
        this.name = name;
        this.view = view;
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

    @Override
    public MenuView getView() {
        return view;
    }
}