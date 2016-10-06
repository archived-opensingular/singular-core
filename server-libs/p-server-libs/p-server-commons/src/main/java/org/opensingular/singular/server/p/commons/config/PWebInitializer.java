package org.opensingular.singular.server.p.commons.config;

import org.opensingular.singular.server.commons.config.IServerContext;
import org.opensingular.singular.server.commons.config.WebInitializer;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class PWebInitializer extends WebInitializer {


    @Override
    protected IServerContext[] serverContexts() {
        return PServerContext.values();
    }

}
