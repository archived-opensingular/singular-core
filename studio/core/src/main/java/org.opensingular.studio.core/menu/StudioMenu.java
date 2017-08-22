package org.opensingular.studio.core.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudioMenu {

    private List<MenuEntry> children;

    public StudioMenu() {
        children = new ArrayList<>();
    }

    public <T extends MenuEntry> T add(T child){
        children.add(child);
        return child;
    }

    public List<MenuEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }
}