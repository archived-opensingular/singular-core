package org.opensingular.studio.app.spring;

import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.studio.app.config.StudioAppConfig;
import org.opensingular.studio.core.config.StudioConfigProvider;
import org.opensingular.studio.core.menu.StudioMenu;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.opensingular.lib.support.spring.util", "org.opensingular.studio.app"})
public class StudioSpringConfiguration implements Loggable {
    private final StudioAppConfig studioConfig;

    public StudioSpringConfiguration() {
        studioConfig = (StudioAppConfig) StudioConfigProvider.get().retrieve();
    }

    @Bean
    public StudioMenu studioMenu() {
        return studioConfig.getAppMenu();
    }

    @Bean
    public StudioApplication studioApplication() {
        return studioConfig.getWicketApplication();
    }

    @Bean
    public SingularSingletonStrategy singularSingletonStrategy() {
        return studioConfig.getSingularSingletonStrategy();
    }

    @Bean
    public SDocumentFactory sDocumentFactory() {
        return studioConfig.getSDocumentFactory();
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return studioConfig.getServiceRegistry();
    }
}