package org.opensingular.studio.app;

import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.core.menu.StudioMenu;
import org.springframework.context.annotation.Bean;

public abstract class StudioAppConfig {
    @Bean
    public StudioApplication getWicketApplication() {
        return new StudioApplication();
    }

    @Bean
    public StudioMenu menu() {
        return new StudioMenu();
    }
}