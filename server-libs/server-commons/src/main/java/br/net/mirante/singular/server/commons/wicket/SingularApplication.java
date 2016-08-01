package br.net.mirante.singular.server.commons.wicket;

import java.util.Locale;

import br.net.mirante.singular.server.commons.wicket.error.Page500;
import br.net.mirante.singular.server.commons.wicket.error.Page500Content;
import br.net.mirante.singular.server.commons.wicket.listener.SingularServerContextListener;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.ExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import br.net.mirante.singular.util.wicket.page.error.Error403Page;

public abstract class SingularApplication extends AuthenticatedWebApplication
        implements ApplicationContextAware {

    public static final String BASE_FOLDER = "/tmp/fileUploader";

    private Class<? extends WebPage> homePageClass;

    private ApplicationContext ctx;

    public static SingularApplication get() {
        return (SingularApplication) WebApplication.get();
    }

    @Override
    public void init() {
        super.init();

        getRequestCycleSettings().setTimeout(Duration.minutes(5));
        getRequestCycleListeners().add(new SingularServerContextListener());

        Locale.setDefault(new Locale("pt", "BR"));

        getApplicationSettings().setAccessDeniedPage(Error403Page.class);

        // Don't forget to check your Application server for this
        getApplicationSettings().setDefaultMaximumUploadSize(Bytes.megabytes(10));
        getApplicationSettings().setInternalErrorPage(Page500.class);
        getExceptionSettings().setUnexpectedExceptionDisplay(ExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);

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

        new AnnotatedMountScanner().scanPackage("br.net.mirante").mount(this);

        for (String packageName : getPackagesToScan()){
            new AnnotatedMountScanner().scanPackage(packageName).mount(this);
        }

    }

    @Override
    public Session newSession(Request request, Response response) {
        return new SingularSession(request, response);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return SingularSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return (Class<? extends WebPage>) getHomePage();
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if ("false".equals(System.getProperty("singular.development"))) {
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

    /**
     * @return Package a ser escaneada pelo {@link AnnotatedMountScanner} para buscar pelos mounts das páginas.
     */
    protected abstract String[] getPackagesToScan();

}

