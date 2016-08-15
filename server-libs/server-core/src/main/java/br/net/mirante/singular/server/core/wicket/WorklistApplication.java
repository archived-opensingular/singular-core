package br.net.mirante.singular.server.core.wicket;

import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import br.net.mirante.singular.server.core.wicket.entrada.CaixaEntradaAnalisePage;
import org.apache.wicket.Page;

public class WorklistApplication extends SingularApplication {
    @Override
    protected String[] getPackagesToScan() {
        return new String[]{"br.net.mirante.singular"};
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return CaixaEntradaAnalisePage.class;
    }
}
