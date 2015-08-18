package br.net.mirante.singular;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import br.net.mirante.singular.view.page.processo.ProcessosPage;
import br.net.mirante.singular.view.template.Template;

public class UIAdminApplication extends WebApplication {

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return Template.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        // add your configuration here
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        mountPage("processos", ProcessosPage.class);
    }
}
