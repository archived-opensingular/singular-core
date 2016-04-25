package br.net.mirante.singular.server.core.config;

import br.net.mirante.singular.server.commons.config.SpringHibernateInitializer;
import br.net.mirante.singular.server.commons.spring.SingularServerSpringAppConfig;
import br.net.mirante.singular.server.core.spring.DefaultBeansFactory;
import br.net.mirante.singular.server.core.spring.DefaultPersistenceConfiguration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

public class WSpringHibernateInitializer extends SpringHibernateInitializer {


    @Override
    public AnnotationConfigWebApplicationContext init(ServletContext ctx) {
        AnnotationConfigWebApplicationContext applicationContext = super.init(ctx);
        applicationContext.register(defaultWorklistBeansFactory());
        applicationContext.register(persistenceConfiguration());
        return applicationContext;
    }

    @Override
    protected Class<? extends SingularServerSpringAppConfig> springConfigurationClass() {
        return SingularServerSpringAppConfig.class;
    }


    protected Class<?> defaultWorklistBeansFactory() {
        return DefaultBeansFactory.class;
    }


    protected Class<?> persistenceConfiguration() {
        return DefaultPersistenceConfiguration.class;
    }


}
