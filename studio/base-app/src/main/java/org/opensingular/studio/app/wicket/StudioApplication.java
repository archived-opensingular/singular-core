package org.opensingular.studio.app.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.opensingular.lib.wicket.util.template.SingularTemplate;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminApp;
import org.opensingular.lib.wicket.util.template.admin.SingularAdminTemplate;
import org.opensingular.studio.app.AbstractStudioAppConfig;
import org.opensingular.studio.app.wicket.pages.StudioHeader;
import org.opensingular.studio.app.wicket.pages.StudioPage;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class StudioApplication extends WebApplication implements SingularAdminApp {

    private final AbstractStudioAppConfig appConfig;

    public StudioApplication(AbstractStudioAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private final static class OuputMarkupBehavior extends Behavior {
        @Override
        public void onConfigure(Component comp) {
            if (!comp.getRenderBodyOnly()) {
                comp
                        .setOutputMarkupId(true)
                        .setOutputMarkupPlaceholderTag(true);
            }
        }
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return StudioPage.class;
    }

    @Override
    protected void init() {
        super.init();
        Locale.setDefault(new Locale("pt", "BR"));
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setStripComments(true);
        getMarkupSettings().setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        setHeaderResponseDecorator(r -> new JavaScriptFilteredIntoFooterHeaderResponse(r, SingularTemplate.JAVASCRIPT_CONTAINER));
        getComponentInitializationListeners().add(comp -> comp.add(new OuputMarkupBehavior()));
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        new AnnotatedMountScanner().scanPackage("org.opensingular.studio.app").mount(this);
        List<IStringResourceLoader> stringResourceLoaders = getResourceSettings().getStringResourceLoaders();
        stringResourceLoaders.add(0, new ClassStringResourceLoader(appConfig.getClass()));
    }

    @Override
    public MarkupContainer buildPageHeader(String id,
                                           boolean withMenu,
                                           SingularAdminTemplate adminTemplate) {
        return new StudioHeader(id);
    }
}