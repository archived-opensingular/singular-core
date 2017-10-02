package org.opensingular.studio.app.init;

import org.apache.wicket.protocol.http.WicketFilter;
import org.opensingular.studio.app.config.StudioAppConfig;
import org.opensingular.studio.core.config.StudioConfigProvider;
import org.opensingular.studio.core.wicket.StudioApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Nonnull;
import javax.servlet.*;
import java.util.EnumSet;

public class StudioWebAppInitializer implements WebApplicationInitializer {

    private StudioAppConfig studioConfig;

    public StudioWebAppInitializer() {
        studioConfig = (StudioAppConfig) StudioConfigProvider.get().retrieve();
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = createContext();
        configureContext(container, rootContext);
        addSpringMVCServlet(container, rootContext);
        addWicketFilter(container, rootContext);
        addSpringSecurityFilter(container, rootContext);
    }

    private void configureContext(ServletContext container, AnnotationConfigWebApplicationContext rootContext) {
        rootContext.setServletContext(container);
        container.addListener(new ContextLoaderListener(rootContext));
        studioConfig.getSpringAnnotatedConfigs().forEach(rootContext::register);
        rootContext.refresh();
    }

    private void addWicketFilter(ServletContext container, AnnotationConfigWebApplicationContext rootContext) {
        WicketFilter wicketFilter = new WicketFilter(rootContext.getBean(StudioApplication.class));
        wicketFilter.setFilterPath("");
        FilterRegistration.Dynamic filterRegistration = container.addFilter("wicketFilter", wicketFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "*");
    }

    @Nonnull
    private AnnotationConfigWebApplicationContext createContext() {
        return new AnnotationConfigWebApplicationContext();
    }

    private void addSpringMVCServlet(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ServletRegistration.Dynamic dispatcher = ctx
                .addServlet("Spring MVC dispatcher servlet", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

    private void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }
}