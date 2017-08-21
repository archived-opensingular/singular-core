package org.opensingular.studio.app.wicket.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;
import org.opensingular.lib.wicket.util.menu.MetronicMenu;
import org.opensingular.lib.wicket.util.menu.MetronicMenuGroup;
import org.opensingular.lib.wicket.util.menu.MetronicMenuItem;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;

public class StudioTemplate extends SingularAdminTemplate {

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
        return true;
    }

    @NotNull
    @Override
    protected WebMarkupContainer buildPageMenu(String id) {
        MetronicMenu metronicMenu = new MetronicMenu(id);
        MetronicMenuGroup group = new MetronicMenuGroup(DefaultIcons.CHAIN, "Foo");
        group.addItem(new MetronicMenuItem(DefaultIcons.BUG, "Bar", WelcomePage.class));
        metronicMenu.addItem(group);
        return metronicMenu;
    }
}
