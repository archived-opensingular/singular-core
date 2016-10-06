package org.opensingular.singular.server.core.wicket;

import org.opensingular.singular.server.commons.wicket.SingularApplication;
import org.opensingular.singular.server.core.wicket.entrada.CaixaEntradaAnalisePage;
import org.apache.wicket.Page;

public class WorklistApplication extends SingularApplication {
    @Override
    protected String[] getPackagesToScan() {
        return new String[]{"org.opensingular.singular"};
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return CaixaEntradaAnalisePage.class;
    }
}
