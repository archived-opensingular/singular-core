/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.studio.core.menu;

import org.opensingular.lib.commons.lambda.IBiFunction;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.view.StudioContent;
import org.opensingular.studio.core.view.StudioMenuEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GroupMenuEntry extends AbstractMenuEntry {
    private final List<MenuEntry> children;
    private final MenuView        menuView;

    public GroupMenuEntry(Icon icon, String name, MenuView menuView) {
        super(icon, name);
        this.children = new ArrayList<>();
        this.menuView = menuView;
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

        public Builder addSidebarGroup(Icon icon, String name, Consumer<Builder> groupConsumer) {
            GroupMenuEntry g = groupEntry.add(new GroupMenuEntry(icon, name, MenuView.SIDEBAR));
            if (groupConsumer != null) {
                groupConsumer.accept(new GroupMenuEntry.Builder(g));
            }
            return this;
        }

        public Builder addStudioItemWithMenu(String name, IBiFunction<String, MenuEntry, StudioContent> contentFactory) {
            groupEntry.add(new StudioMenuEntry(null, name, contentFactory, true));
            return this;
        }

        public Builder addStudioItemWithoutMenu(String name, IBiFunction<String, MenuEntry, StudioContent> contentFactory) {
            groupEntry.add(new StudioMenuEntry(null, name, contentFactory, false));
            return this;
        }

        public Builder addStudioItem(String name, StudioDefinition definition) {
            groupEntry.add(new StudioCRUDMenuEntry(null, name, definition));
            return this;
        }

        public Builder addHTTPEndpoint(Icon ico, String name, String endpoint) {
            groupEntry.add(new UrlMenuEntry(ico, name, endpoint));
            return this;
        }
    }

    public MenuView getMenuView() {
        return menuView;
    }

    @Override
    public StudioContent makeContent(String id) {
        return getMenuView().makeStudioContent(id, this);
    }

    @Override
    public boolean isWithMenu() {
        return menuView == MenuView.SIDEBAR;
    }
}