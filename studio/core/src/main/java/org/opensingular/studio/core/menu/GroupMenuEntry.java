package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.definition.StudioDefinition;

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

    public static class Builder {
        private GroupMenuEntry groupEntry;

        public Builder(GroupMenuEntry groupEntry) {
            this.groupEntry = groupEntry;
        }

        public Builder addStudioItem(String name, StudioDefinition definition) {
            groupEntry.add(new ItemMenuEntry(name, new StudioMenuView(definition)));
            return this;
        }

        public Builder addHTTPEndpoint(Icon ico, String name, String endpoint) {
            groupEntry.add(new ItemMenuEntry(ico, name, new HTTPEndpointMenuView(endpoint)));
            return this;
        }
    }

}