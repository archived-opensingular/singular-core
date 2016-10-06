package org.opensingular.server.core.wicket;

import org.opensingular.server.commons.wicket.SingularApplication;
import org.opensingular.server.core.wicket.entrada.CaixaEntradaAnalisePage;
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
