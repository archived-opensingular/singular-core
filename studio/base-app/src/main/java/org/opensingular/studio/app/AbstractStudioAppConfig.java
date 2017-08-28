package org.opensingular.studio.app;

import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.spring.SpringSDocumentFactoryEmpty;
import org.opensingular.form.spring.SpringServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.lib.context.singleton.SpringBoundedSingletonStrategy;
import org.opensingular.studio.app.spring.DefaulSpringSecurityConfig;
import org.opensingular.studio.app.wicket.StudioApplication;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStudioAppConfig implements StudioAppConfig {
    @Override
    public StudioApplication getWicketApplication() {
        return new StudioApplication();
    }

    @Override
    public List<Class<?>> getSpringAnnotatedConfigs() {
        List<Class<?>> springConfigs = new ArrayList<>();
        springConfigs.add(getSpringConfig());
        springConfigs.addAll(getSpringSecurityConfig());
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

    public abstract Class<?> getSpringConfig();

    public List<Class<?>> getSpringSecurityConfig(){
        List<Class<?>> configs = new ArrayList<>();
        configs.add(DefaulSpringSecurityConfig.class);
        return configs;
    }

}