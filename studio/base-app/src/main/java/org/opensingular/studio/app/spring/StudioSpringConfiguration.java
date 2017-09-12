package org.opensingular.studio.app.spring;

import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.studio.app.StudioAppConfig;
import org.opensingular.studio.app.wicket.StudioApplication;
import org.opensingular.studio.app.menu.StudioMenu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"org.opensingular.lib.support.spring.util", "org.opensingular.studio.app"})
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

    @Bean
    public SingularSingletonStrategy singularSingletonStrategy() {
        return studioAppConfig.getSingularSingletonStrategy();
    }

    @Bean
    public SDocumentFactory sDocumentFactory() {
        return studioAppConfig.getSDocumentFactory();
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return studioAppConfig.getServiceRegistry();
    }
}