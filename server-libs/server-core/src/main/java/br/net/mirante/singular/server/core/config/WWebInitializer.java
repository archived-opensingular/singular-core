package br.net.mirante.singular.server.core.config;

import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.config.WebInitializer;
import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import br.net.mirante.singular.server.core.wicket.WorklistApplication;

public class WWebInitializer extends WebInitializer {
    @Override
    protected Class<? extends SingularApplication> getWicketApplicationClass(IServerContext context) {
        return WorklistApplication.class;
    }
}
