package org.opensingular.studio.app.init;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jetbrains.annotations.NotNull;
import org.opensingular.studio.app.StudioAppConfig;
import org.opensingular.studio.app.spring.StudioAppConfigProvider;
import org.opensingular.studio.app.spring.StudioSpringConfiguration;
import org.opensingular.studio.app.spring.StudioWebConfiguration;
import org.opensingular.studio.app.wicket.StudioApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

public class StudioWebAppInitializer implements WebApplicationInitializer {

    private StudioAppConfig studioAppConfig;

    public StudioWebAppInitializer() {
        studioAppConfig = StudioAppConfigProvider.get().retrieve();
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = createContext();
        container.addListener(new ContextLoaderListener(rootContext));
        rootContext.scan("org.opensingular.studio.app");
        rootContext.register(StudioWebConfiguration.class);
        rootContext.setServletContext(container);
        addSpringMVCServlet(container, rootContext);
        studioAppConfig.getSpringAnnotatedConfigs().forEach(rootContext::register);
        addSpringSecurityFilter(container, rootContext);
        rootContext.register(StudioSpringConfiguration.class);
        rootContext.refresh();
        addWicketFilter(container, rootContext);
    }

    private void addWicketFilter(ServletContext container, AnnotationConfigWebApplicationContext rootContext) {
        WicketFilter wicketFilter = new WicketFilter(rootContext.getBean(StudioApplication.class));
        wicketFilter.setFilterPath("");
        FilterRegistration.Dynamic filterRegistration = container.addFilter("wicketFilter", wicketFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "*");
    }

    @NotNull
    private AnnotationConfigWebApplicationContext createContext() {
        return new AnnotationConfigWebApplicationContext();
    }

    protected void addSpringMVCServlet(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ServletRegistration.Dynamic dispatcher = ctx
                .addServlet("Spring MVC dispatcher servlet", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
    }

    protected void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }
}