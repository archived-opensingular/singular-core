package org.opensingular.studio.app.menu;

import org.opensingular.studio.core.menu.MenuView;

public class SimpleUrlMenuView implements MenuView {
    private final String url;

    public SimpleUrlMenuView(String url) {
        this.url = url;
    }

    @Override
    public String getEndpoint(String menuPath) {
        return url;
    }
}
