package org.opensingular.studio.app.spring;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.studio.app.StudioAppConfig;
import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.core.menu.StudioMenu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudioSpringConfiguration implements Loggable {
    private final StudioAppConfig studioAppConfig;

    public StudioSpringConfiguration() {
        studioAppConfig = StudioAppConfigProvider.get().retrieve();
    }

    @Bean
    public StudioMenu studioMenu() {
        return studioAppConfig.getAppMenu();
    }

    @Bean
    public StudioApplication studioApplication() {
        return studioAppConfig.getWicketApplication();
    }
}