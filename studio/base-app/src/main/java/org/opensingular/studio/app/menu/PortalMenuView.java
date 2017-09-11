package org.opensingular.studio.app.menu;

import org.apache.wicket.protocol.http.WebApplication;
import org.opensingular.studio.core.menu.MenuView;

public class PortalMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        return WebApplication.get().getServletContext().getContextPath() + "/home/" + menuPath;
    }
}