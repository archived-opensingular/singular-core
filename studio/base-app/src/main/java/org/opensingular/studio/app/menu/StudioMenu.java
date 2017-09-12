package org.opensingular.studio.app.menu;

import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.MenuView;

import java.util.*;
import java.util.function.Consumer;

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

    public static class Builder {
        private StudioMenu studioMenu;

        public Builder(StudioMenu studioMenu) {
            this.studioMenu = studioMenu;
        }

        public Builder addHTTPEndpoint(Icon icon, String name, String endpoint) {
            ItemMenuEntry i = studioMenu.add(new ItemMenuEntry(icon, name, new HTTPEndpointMenuView(endpoint)));
            return this;
        }

        public Builder addSidebarGroup(Icon icon, String name, Consumer<GroupMenuEntry.Builder> groupConsumer) {
            GroupMenuEntry g = studioMenu.add(new GroupMenuEntry(icon, name, new SidebarMenuView()));
            if (groupConsumer != null) {
                groupConsumer.accept(new GroupMenuEntry.Builder(g));
            }
            return this;
        }

        public Builder addPortalGroup(Icon icon, String name, Consumer<GroupMenuEntry.Builder> groupConsumer) {
            GroupMenuEntry g = studioMenu.add(new GroupMenuEntry(icon, name, new PortalMenuView()));
            if (groupConsumer != null) {
                groupConsumer.accept(new GroupMenuEntry.Builder(g));
            }
            return this;
        }

        public static Builder newPortalMenu() {
            return new Builder(new StudioMenu(new PortalMenuView()));
        }

        public static Builder newSidebarMenu() {
            return new Builder(new StudioMenu(new SidebarMenuView()));
        }

        public StudioMenu getStudioMenu() {
            return studioMenu;
        }
    }

}