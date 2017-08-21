package org.opensingular.studio.app;

import org.opensingular.studio.app.wicket.StudioApplication;
import org.springframework.context.annotation.Bean;

public class StudioAppConfig {
    @Bean
    public StudioApplication getWicketApplication(){
        return new StudioApplication();
    }
}