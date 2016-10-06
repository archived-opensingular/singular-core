package org.opensingular.singular.server.module.wicket;

import org.opensingular.singular.server.commons.wicket.SingularApplication;
import org.apache.wicket.Page;

public class ModuleApplication extends SingularApplication {
    @Override
    protected String[] getPackagesToScan() {
        return new String[]{"org.opensingular.singular"};
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return null;
    }
}
