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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import br.net.mirante.singular.view.error.Error403Page;
import br.net.mirante.singular.view.page.form.FormPage;

public class ShowcaseApplication extends AuthenticatedWebApplication implements ApplicationContextAware {

    private ApplicationContext ctx;

	@Override
    public Class<? extends WebPage> getHomePage() {
        return FormPage.class;
    }

    @Override
    public void init() {
        super.init();

        Locale.setDefault(new Locale("pt", "BR"));

        getApplicationSettings().setAccessDeniedPage(Error403Page.class);

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        if(ctx != null){
        	getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        }else{
        	getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        }
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
        return FormPage.class;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (System.getProperty("singular.development") != null) {
            return RuntimeConfigurationType.DEVELOPMENT;
        } else {
            return RuntimeConfigurationType.DEPLOYMENT;
        }
    }

    public static ShowcaseApplication get() {
        return (ShowcaseApplication) WebApplication.get();
    }

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}
}
