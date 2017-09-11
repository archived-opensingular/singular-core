package org.opensingular.studio.app.menu;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.opensingular.studio.app.wicket.pages.StudioPortalPage;
import org.opensingular.studio.core.menu.MenuView;

public class PortalMenuView implements MenuView {
    @Override
    public String getEndpoint(String menuPath) {
        String[] paths = menuPath.split("/");
        StringBuilder path = new StringBuilder();
        if (paths.length > 0) {
            path.append((String) RequestCycle.get().urlFor(StudioPortalPage.class, new PageParameters().add("path", paths[0])));
            for (int i = 1; i < paths.length; i++) {
                path.append('/').append(paths[i]);
            }
        }
        return WebApplication.get().getServletContext().getContextPath() + path.toString();
    }
}