package br.net.mirante.singular.wicket;

import java.util.Locale;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import br.net.mirante.singular.view.error.Error403Page;
import br.net.mirante.singular.view.page.dashboard.DashboardPage;

public class UIAdminApplication extends AuthenticatedWebApplication {

    @Override
    public Class<? extends WebPage> getHomePage() {
        return DashboardPage.class;
    }

    @Override
    public void init() {
        super.init();

        Locale.setDefault(new Locale("pt", "BR"));

        getApplicationSettings().setAccessDeniedPage(Error403Page.class);

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        new AnnotatedMountScanner().scanPackage("br.net.mirante.singular.view.page.**").mount(this);
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new UIAdminSession(request, response);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return UIAdminSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return DashboardPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (System.getProperty("singular.development") != null) {
            return RuntimeConfigurationType.DEVELOPMENT;
       } else {
            return RuntimeConfigurationType.DEPLOYMENT;
        }
    }

    public static UIAdminApplication get() {
        return (UIAdminApplication) WebApplication.get();
    }
}
