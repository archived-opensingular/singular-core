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

package org.opensingular.studio.core.view;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.lib.wicket.util.menu.AbstractMenuItem;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.MenuView;
import org.opensingular.studio.core.menu.StudioMenu;
import org.wicketstuff.annotation.mount.MountPath;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@MountPath("/studio/${path}")
public class StudioPage extends SingularAdminTemplate {

    public static final String STUDIO_CONTENT = "studioContent";

    @Inject
    private StudioMenu studioMenu;

    private String menuPath;

    public StudioPage() {
        this(new PageParameters());
    }

    public StudioPage(PageParameters parameters) {
        super(parameters);
        resolveMenuPath();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        MenuEntry          currentMenuEntry = findCurrentMenuEntry();
        WebMarkupContainer studioContent    = null;
        if (currentMenuEntry != null) {
            studioContent = currentMenuEntry.makeContent(STUDIO_CONTENT);
        }
        if (studioContent == null) {
            studioContent = studioMenu.getView().makeStudioContent(STUDIO_CONTENT, null);
        }
        add(studioContent);
    }

    private void resolveMenuPath() {
        StringValue pathStringValue = getPageParameters().get("path");
        if (pathStringValue.isNull() || pathStringValue.isEmpty()) {
            menuPath = "";
        }
        else {
            StringBuilder path = new StringBuilder(pathStringValue.toString());
            for (int i = 0; i < getPageParameters().getIndexedCount(); i++) {
                path.append('/').append(getPageParameters().get(i));
            }
            menuPath = path.toString();
        }
    }

    private MenuEntry findCurrentMenuEntry() {
        return findCurrentMenuEntry(getStudioMenu().getChildren());
    }

    private MenuEntry findCurrentMenuEntry(List<MenuEntry> entries) {
        if (StringUtils.isBlank(getMenuPath())) {
            return null;
        }
        for (MenuEntry entry : entries) {
            if (entry.getMenuPath().endsWith(getMenuPath())) {
                return entry;
            }
            if (entry instanceof GroupMenuEntry) {
                MenuEntry foundedEntry = findCurrentMenuEntry(((GroupMenuEntry) entry).getChildren());
                if (foundedEntry != null) {
                    return foundedEntry;
                }
            }
        }
        return null;
    }

    @Override
    protected IModel<String> getContentTitle() {
        return null;
    }

    @Override
    protected IModel<String> getContentSubtitle() {
        return null;
    }

    @Override
    protected boolean isWithMenu() {
        MenuEntry currentMenuEntry = findCurrentMenuEntry();
        return (currentMenuEntry == null && studioMenu.getView() == MenuView.SIDEBAR) || currentMenuEntry != null && currentMenuEntry.isWithMenu();
    }

    private String getMenuPath() {
        if (menuPath == null) {
            resolveMenuPath();
        }
        return menuPath;
    }

    private static class StudioTemplateException extends RuntimeException {
        public StudioTemplateException(String s) {
            super(s);
        }
    }

    private StudioMenu getStudioMenu() {
        return studioMenu;
    }

    @Nonnull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        MetronicMenu metronicMenu     = new MetronicMenu(id);
        MenuEntry    currentMenuEntry = findCurrentMenuEntry();
        if (currentMenuEntry != null) {
            while (currentMenuEntry instanceof ItemMenuEntry) {
                currentMenuEntry = currentMenuEntry.getParent();
            }
            AbstractMenuItem menu = buildMenu(currentMenuEntry, true);
            if (menu instanceof MetronicMenuGroup) {
                MetronicMenuGroup metronicMenuGroup = (MetronicMenuGroup) menu;
                metronicMenuGroup.setOpen();
            }
            metronicMenu.addItem(menu);
        }
        else {
            studioMenu.getChildren().forEach(e -> metronicMenu.addItem(buildMenu(e, false)));
        }
        return metronicMenu;
    }

    private AbstractMenuItem buildMenu(MenuEntry menuEntry, boolean groupNestedSidebars) {
        if (menuEntry instanceof GroupMenuEntry) {
            GroupMenuEntry group = (GroupMenuEntry) menuEntry;
            if (groupNestedSidebars && (group.getMenuView() == null || group.getMenuView() == MenuView.SIDEBAR)) {
                MetronicMenuGroup metronicMenuGroup = new MetronicMenuGroup(menuEntry.getIcon(), menuEntry.getName());
                for (MenuEntry child : group.getChildren()) {
                    metronicMenuGroup.addItem(buildMenu(child, true));
                }
                return metronicMenuGroup;
            }
        }
        return new MetronicMenuItem(menuEntry.getIcon(), menuEntry.getName(), menuEntry.getEndpoint());
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return Optional.ofNullable(findCurrentMenuEntry())
                .map(MenuEntry::getName)
                .map(Model::new)
                .orElse(new Model<>());
    }
}