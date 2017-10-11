package org.opensingular.studio.core.wicket;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.application.SkinnableApplication;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.opensingular.lib.wicket.util.template.SkinOptions;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.studio.core.config.StudioConfig;
import org.opensingular.studio.core.view.StudioFooter;
import org.opensingular.studio.core.view.StudioHeader;
import org.opensingular.studio.core.view.StudioPage;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class StudioApplication extends WebApplication implements SingularAdminApp, SkinnableApplication {
    private final StudioConfig appConfig;

    public StudioApplication(StudioConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return StudioPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setLocale(new Locale("pt", "BR"));
        return session;
    }

    @Override
    protected void init() {
        super.init();
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        setHeaderResponseDecorator(r -> new JavaScriptFilteredIntoFooterHeaderResponse(r, SingularTemplate.JAVASCRIPT_CONTAINER));
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        new AnnotatedMountScanner().scanPackage("org.opensingular.studio").mount(this);
        List<IStringResourceLoader> stringResourceLoaders = getResourceSettings().getStringResourceLoaders();
        stringResourceLoaders.add(0, new ClassStringResourceLoader(appConfig.getClass()));
        getComponentOnConfigureListeners().add(component -> {
            boolean outputId = !component.getRenderBodyOnly();
            component.setOutputMarkupId(outputId).setOutputMarkupPlaceholderTag(outputId);
        });
    }

    @Override
    public MarkupContainer buildPageHeader(String id,
                                           boolean withMenu,
                                           SingularAdminTemplate adminTemplate) {
        return new StudioHeader(id);
    }

    @Override
    public MarkupContainer buildPageFooter(String id) {
        return new StudioFooter(id);
    }

    @Override
    public void initSkins(SkinOptions skinOptions) {
        IConsumer<SkinOptions> initSKin = (IConsumer<SkinOptions>) this.getServletContext()
                .getAttribute(SkinnableApplication.INITSKIN_CONSUMER_PARAM);
        if (initSKin != null) {
            initSKin.accept(skinOptions);
        }
    }
}