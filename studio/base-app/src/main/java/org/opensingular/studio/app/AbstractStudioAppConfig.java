package org.opensingular.studio.app;

import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.core.menu.StudioMenu;

public abstract class AbstractStudioAppConfig implements StudioAppConfig {
    @Override
    public StudioApplication getWicketApplication() {
        return new StudioApplication();
    }

    @Override
    public StudioMenu getAppMenu() {
        return new StudioMenu();
    }

    @Override
    public Class<?>[] getSpringAnnotatedConfigs() {
        return new Class[0];
    }
}