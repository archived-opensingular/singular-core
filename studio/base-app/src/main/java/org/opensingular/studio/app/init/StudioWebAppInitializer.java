package org.opensingular.studio.app.init;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jetbrains.annotations.NotNull;
import org.opensingular.studio.app.StudioAppConfig;
import org.opensingular.studio.app.spring.StudioAppConfigProvider;
import org.opensingular.studio.app.spring.StudioSpringConfiguration;
import org.opensingular.studio.app.wicket.StudioApplication;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("org.opensingular.studio");
        rootContext.register(studioAppConfig.getSpringAnnotatedConfigs());
        rootContext.register(StudioSpringConfiguration.class);
        rootContext.refresh();
        return rootContext;
    }

}