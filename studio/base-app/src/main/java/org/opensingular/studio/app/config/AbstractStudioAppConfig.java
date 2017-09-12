package org.opensingular.studio.app.config;

import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.spring.SpringSDocumentFactoryEmpty;
import org.opensingular.form.spring.SpringServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.context.singleton.SpringBoundedSingletonStrategy;
import org.opensingular.studio.app.spring.DefaulSpringSecurityConfig;
import org.opensingular.studio.app.spring.StudioPersistenceConfiguration;
import org.opensingular.studio.app.spring.StudioSpringConfiguration;
import org.opensingular.studio.app.spring.StudioWebConfiguration;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStudioAppConfig implements StudioAppConfig {

    @Override
    public StudioApplication getWicketApplication() {
        return new StudioApplication(this);
    }

    @Override
    public List<Class<?>> getSpringAnnotatedConfigs() {
        List<Class<?>> springConfigs = new ArrayList<>();
        springConfigs.add(getSpringConfig());
        springConfigs.add(getSpringWebConfig());
        springConfigs.add(getSpringPersistenceConfig());
        springConfigs.add(getSpringSecurityConfig());
        return springConfigs;
    }

    @Override
    public SingularSingletonStrategy getSingularSingletonStrategy() {
        return new SpringBoundedSingletonStrategy();
    }

    @Override
    public SDocumentFactory getSDocumentFactory() {
        return new SpringSDocumentFactoryEmpty();
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return new SpringServiceRegistry();
    }

    public Class<? extends StudioSpringConfiguration> getSpringConfig() {
        return StudioSpringConfiguration.class;
    }

    public Class<? extends WebSecurityConfigurerAdapter> getSpringSecurityConfig() {
        return DefaulSpringSecurityConfig.class;
    }

    public Class<? extends WebMvcConfigurerAdapter> getSpringWebConfig() {
        return StudioWebConfiguration.class;
    }

    public Class<? extends StudioPersistenceConfiguration> getSpringPersistenceConfig() {
        return StudioPersistenceConfiguration.class;
    }

}