package br.net.mirante.singular.server.module.wicket;

import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import org.apache.wicket.Page;

public class ModuleApplication extends SingularApplication {
    @Override
    protected String[] getPackagesToScan() {
        return new String[]{"br.net.mirante.singular"};
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return null;
    }
}
