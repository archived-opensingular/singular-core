package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupMenuEntry extends AbstractMenuEntry {
    private final List<MenuEntry> children;

    public GroupMenuEntry(Icon icon, String name) {
        super(icon, name);
        children = new ArrayList<>();
    }

    public List<MenuEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public <T extends MenuEntry> T add(T child){
        child.setParent(this);
        this.children.add(child);
        return child;
    }
}