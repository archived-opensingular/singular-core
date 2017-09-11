package org.opensingular.studio.app.menu;

import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.menu.AbstractMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.MenuView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupMenuEntry extends AbstractMenuEntry {
    private final List<MenuEntry> children;

    public GroupMenuEntry(Icon icon, String name, MenuView view) {
        super(icon, name, view);
        this.children = new ArrayList<>();
    }

    public GroupMenuEntry(Icon icon, String name) {
        this(icon, name, null);
    }

    public List<MenuEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public <T extends MenuEntry> T add(T child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }
    
}