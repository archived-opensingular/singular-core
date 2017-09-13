package org.opensingular.studio.core.menu;

import org.opensingular.studio.core.menu.MenuView;

public class HTTPEndpointMenuView implements MenuView {
    private final String url;

    public HTTPEndpointMenuView(String url) {
        this.url = url;
    }

    @Override
    public String getEndpoint(String menuPath) {
        return url;
    }
}
