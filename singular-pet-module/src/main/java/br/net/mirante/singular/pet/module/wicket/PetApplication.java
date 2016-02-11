package br.net.mirante.singular.pet.module.wicket;

import br.net.mirante.singular.pet.module.exception.SingularServerException;
import br.net.mirante.singular.util.wicket.page.error.Error403Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import java.util.Locale;

public class PetApplication extends AuthenticatedWebApplication
        implements ApplicationContextAware {

    public static final String BASE_FOLDER = "/tmp/fileUploader";

    private Class<? extends WebPage> homePageClass;

    private ApplicationContext ctx;

    public static PetApplication get() {
        return (PetApplication) WebApplication.get();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        if (homePageClass == null) {
            String pageClass = this.getInitParameter("homePageClass");
            if (StringUtils.isEmpty(pageClass)) {
                throw new SingularServerException("O parâmetro homePageClass não foi definido. Defina no web.xml a Classe que representa a página inicial.");
            }
            try {
                homePageClass = (Class<? extends WebPage>) Class.forName(pageClass);
            } catch (ClassNotFoundException e) {
                throw new SingularServerException("Não foi possível encontrar a classe definida no parâmetro homePageClass . Defina no web.xml a Classe que representa a página inicial.", e);
            }
        }
        return homePageClass;
    }

    @Override
    public void init() {
        super.init();

        setHeaderResponseDecorator(new SingularHeaderResponseDecorator());
        Locale.setDefault(new Locale("pt", "BR"));

        getApplicationSettings().setAccessDeniedPage(Error403Page.class);

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getComponentInitializationListeners().add(c -> {
            if (!c.getRenderBodyOnly()) {
                c.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
            }
        });

        if (ctx != null) {
            getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx, true));
        } else {
            getComponentInstantiationListeners().add(new SpringComponentInjector(this));
            ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        }
        new AnnotatedMountScanner().scanPackage("br.**").mount(this);

        /* Desabiltando jquery */
        getJavaScriptLibrarySettings()
                .setJQueryReference(new JavaScriptResourceReference(PetApplication.class, "empty.js"));
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new PetSession(request, response);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return PetSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return getHomePage();
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (System.getProperty("singular.pet.server.deployment") != null) {
            return RuntimeConfigurationType.DEPLOYMENT;
        } else {
            return RuntimeConfigurationType.DEVELOPMENT;
        }
    }

    public ApplicationContext getApplicationContext() {
        return ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

}
