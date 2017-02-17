package org.opensingular.server.commons.wicket;

import org.apache.wicket.Page;
import org.opensingular.server.commons.admin.healthsystem.HealthSystemPage;


public class AdministrationApplication extends SingularApplication {

    @Override
    protected String[] getPackagesToScan() {
        return new String[]{"org.opensingular.server.commons.admin"};
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HealthSystemPage.class;
    }

}