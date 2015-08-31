package br.net.mirante.singular.wicket;

import java.util.Locale;
import java.util.logging.Logger;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import br.net.mirante.singular.view.page.dashboard.DashboardPage;
import br.net.mirante.singular.view.page.processo.ProcessosPage;

import javax.servlet.http.HttpServletRequest;

public class UIAdminApplication extends WebApplication {

    @Override
    public Class<? extends WebPage> getHomePage() {
        return DashboardPage.class;
    }

    @Override
    public void init() {
        super.init();

        Locale.setDefault(new Locale("pt", "BR"));

        // add your configuration here
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        mountPage("processos", ProcessosPage.class);
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new UIAdminSession(request, response);
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (System.getProperty("singular.production") != null) {
            return RuntimeConfigurationType.DEPLOYMENT;
        } else {
            return RuntimeConfigurationType.DEVELOPMENT;
        }
    }
}
