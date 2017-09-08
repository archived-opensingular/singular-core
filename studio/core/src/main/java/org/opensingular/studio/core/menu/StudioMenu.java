package org.opensingular.studio.core.menu;

import java.util.*;

public class StudioMenu {

    private List<MenuEntry> children;
    private boolean portal;
    private GroupMenuView view;

    public StudioMenu(GroupMenuView view) {
        this.view = view;
        this.children = new ArrayList<>();
        this.portal = false;
    }

    public <T extends MenuEntry> T add(T child){
        children.add(child);
        return child;
    }

    public List<MenuEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public boolean isPortal() {
        return portal;
    }

    public void setPortal(boolean portal) {
        this.portal = portal;
    }

    public GroupMenuView getView() {
        return view;
    }
}