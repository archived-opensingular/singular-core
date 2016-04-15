package br.net.mirante.singular.server.p.commons.config;

import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.config.WebInitializer;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class PWebInitializer extends WebInitializer {


    @Override
    protected IServerContext[] getServerContexts() {
        return PServerContext.values();
    }

}
