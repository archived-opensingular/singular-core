package br.net.mirante.singular;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import br.net.mirante.singular.view.page.dashboard.DashboardPage;
import br.net.mirante.singular.view.page.form.FormPage;
import br.net.mirante.singular.view.page.processo.ProcessosPage;

public class UIAdminApplication extends WebApplication {

    @Override
    public Class<? extends WebPage> getHomePage() {
        return DashboardPage.class;
    }

    @Override
    public void init() {
        super.init();

        // add your configuration here
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        mountPage("processos", ProcessosPage.class);
        mountPage("form", FormPage.class);
    }
}
