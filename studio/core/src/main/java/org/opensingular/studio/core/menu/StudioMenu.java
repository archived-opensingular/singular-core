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

import org.opensingular.lib.commons.ui.Icon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StudioMenu {

    private List<MenuEntry> children;
    private MenuView        view;

    public StudioMenu(MenuView view) {
        this.view = view;
        this.children = new ArrayList<>();
    }

    public <T extends MenuEntry> T add(T child) {
        children.add(child);
        return child;
    }

    public List<MenuEntry> getChildren() {
        if (children != null) {
            return Collections.unmodifiableList(children);
        }
        return Collections.emptyList();
    }

    public static class Builder {
        private StudioMenu studioMenu;

        public Builder(StudioMenu studioMenu) {
            this.studioMenu = studioMenu;
        }

        public Builder addHTTPEndpoint(Icon icon, String name, String endpoint) {
            studioMenu.add(new UrlMenuEntry(icon, name, endpoint));
            return this;
        }

        public Builder addSidebarGroup(Icon icon, String name, Consumer<GroupMenuEntry.Builder> groupConsumer) {
            GroupMenuEntry g = studioMenu.add(new GroupMenuEntry(icon, name, MenuView.SIDEBAR));
            if (groupConsumer != null) {
                groupConsumer.accept(new GroupMenuEntry.Builder(g));
            }
            return this;
        }

        public Builder addPortalGroup(Icon icon, String name, Consumer<GroupMenuEntry.Builder> groupConsumer) {
            GroupMenuEntry g = studioMenu.add(new GroupMenuEntry(icon, name, MenuView.PORTAL));
            if (groupConsumer != null) {
                groupConsumer.accept(new GroupMenuEntry.Builder(g));
            }
            return this;
        }

        public static Builder newPortalMenu() {
            return new Builder(new StudioMenu(MenuView.PORTAL));
        }

        public static Builder newSidebarMenu() {
            return new Builder(new StudioMenu(MenuView.SIDEBAR));
        }

        public StudioMenu getStudioMenu() {
            return studioMenu;
        }
    }

    public MenuView getView() {
        return view;
    }
}