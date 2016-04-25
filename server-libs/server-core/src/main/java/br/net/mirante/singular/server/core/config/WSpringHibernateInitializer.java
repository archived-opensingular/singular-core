package br.net.mirante.singular.server.core.config;

import br.net.mirante.singular.server.commons.config.SpringHibernateInitializer;
import br.net.mirante.singular.server.commons.spring.DefaultPersistenceConfiguration;
import br.net.mirante.singular.server.commons.spring.SingularDefaultBeanFactory;

public class WSpringHibernateInitializer extends SpringHibernateInitializer {
    @Override
    protected Class<?> beanFactory() {
        return SingularDefaultBeanFactory.class;
    }

    @Override
    protected Class<?> persistenceConfiguration() {
        return DefaultPersistenceConfiguration.class;
    }
}
