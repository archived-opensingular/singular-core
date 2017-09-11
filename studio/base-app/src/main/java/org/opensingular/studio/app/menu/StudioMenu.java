package org.opensingular.studio.app.menu;

import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.MenuView;

import java.util.*;

public class StudioMenu {

    private List<MenuEntry> children;
    private MenuView view;

    public StudioMenu(MenuView view) {
        this.view = view;
        this.children = new ArrayList<>();
    }

    public <T extends MenuEntry> T add(T child){
        children.add(child);
        return child;
    }

    public List<MenuEntry> getChildren() {
        if(children != null) {
            return Collections.unmodifiableList(children);
        }
        return Collections.emptyList();
    }

    public String getEnpoint(){
        return view.getEndpoint("");
    }

}