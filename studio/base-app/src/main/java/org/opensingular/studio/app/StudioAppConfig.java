package org.opensingular.studio.app;

import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.core.menu.StudioMenu;

public interface StudioAppConfig {
    /**
     * @return the Wicket Application
     */
    StudioApplication getWicketApplication();

    /**
     * @return the app menu
     */
    StudioMenu getAppMenu();

    /**
     * Allow register another classes to be registered as spring config
     * @return the configs
     */
    Class<?>[] getSpringAnnotatedConfigs();
}