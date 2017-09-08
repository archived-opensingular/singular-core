package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;

import java.util.LinkedHashMap;
import java.util.Map;

public class GroupBuilder implements StudioMenuBuilder {

    private LinkedHashMap<MenuEntry, StudioMenuBuilder> menuMap;
    private GroupMenuView groupMenuView;

    public GroupBuilder() {
        groupMenuView = GroupMenuView.SIDEBAR;
        menuMap = new LinkedHashMap<>();
    }

    @Override
    public StudioMenu build() {
        StudioMenu studioMenu = new StudioMenu(groupMenuView);
        for (Map.Entry<MenuEntry, StudioMenuBuilder> entry : menuMap.entrySet()) {
            MenuEntry item = entry.getKey();
            if (item instanceof GroupMenuEntry && entry.getValue() != null) {
                StudioMenu subMenu = entry.getValue().build();
                GroupMenuEntry group = (GroupMenuEntry) item;
                subMenu.getChildren().forEach(group::add);
            }
            studioMenu.add(item);
        }
        return studioMenu;
    }

    public GroupBuilder addGroup(Icon icon, String name) {
        return addGroup(new GroupMenuEntry(icon, name));
    }

    public GroupBuilder addGroup(GroupMenuEntry groupMenuEntry) {
        GroupBuilder groupBuilder = new GroupBuilder();
        menuMap.put(groupMenuEntry, groupBuilder);
        return groupBuilder;
    }

    public void addItem(Icon icon, String name, String endpoint) {
        addItem(new ItemMenuEntry(icon, name, endpoint));
    }

    public void addItem(ItemMenuEntry menuEntry) {
        menuMap.put(menuEntry, null);
    }

    public void setView(GroupMenuView groupMenuView) {
        this.groupMenuView = groupMenuView;
    }
}
