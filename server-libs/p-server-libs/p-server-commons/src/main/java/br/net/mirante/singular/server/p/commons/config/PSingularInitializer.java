package br.net.mirante.singular.server.p.commons.config;

import br.net.mirante.singular.server.commons.config.SingularInitializer;

public interface PSingularInitializer extends SingularInitializer {

    public PWebInitializer webConfiguration();

    public PSpringHibernateInitializer springHibernateConfiguration();

    public PFormInitializer formConfiguration();

    public PFlowInitializer flowConfiguration();

    public PSchedulerInitializer schedulerConfiguration();
    
    public PSpringSecurityInitializer springSecurityConfiguration();

}
