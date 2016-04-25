package br.net.mirante.singular.server.core.config;

import br.net.mirante.singular.server.commons.config.SingularInitializer;
import br.net.mirante.singular.server.commons.config.SpringHibernateInitializer;


public interface WSingularInitializer extends SingularInitializer {

    @Override
    default WWebInitializer webConfiguration() {
        return new WWebInitializer();
    }


    @Override
    default SpringHibernateInitializer springHibernateConfiguration() {
        return new WSpringHibernateInitializer();
    }
}
