package org.opensingular.server.module.wicket;

import org.opensingular.server.commons.wicket.SingularApplication;
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
