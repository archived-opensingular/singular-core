package org.opensingular.studio.app.init;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jetbrains.annotations.NotNull;
import org.opensingular.studio.app.StudioAppConfig;
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

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.scan("org.opensingular.studio");
        rootContext.register(getStudioAppConfig());

        container.addListener(new ContextLoaderListener(rootContext));

        rootContext.refresh();

        WicketFilter wicketFilter = new WicketFilter(rootContext.getBean(StudioApplication.class));
        wicketFilter.setFilterPath("");
        FilterRegistration.Dynamic filterRegistration = container.addFilter("wicketFilter", wicketFilter);
        filterRegistration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "*");
    }

    @NotNull
    protected Class<? extends StudioAppConfig> getStudioAppConfig() {
        return StudioAppConfig.class;
    }

}