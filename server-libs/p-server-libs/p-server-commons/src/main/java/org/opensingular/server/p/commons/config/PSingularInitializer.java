package org.opensingular.server.p.commons.config;

import org.opensingular.server.commons.config.SingularInitializer;

public interface PSingularInitializer extends SingularInitializer {

    public PWebInitializer webConfiguration();

    public PSpringHibernateInitializer springHibernateConfiguration();

    public PFormInitializer formConfiguration();

    public PFlowInitializer flowConfiguration();

    public PSpringSecurityInitializer springSecurityConfiguration();

}
