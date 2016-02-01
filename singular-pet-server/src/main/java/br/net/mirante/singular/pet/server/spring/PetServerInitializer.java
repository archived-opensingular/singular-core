package br.net.mirante.singular.pet.server.spring;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import br.net.mirante.singular.pet.server.wicket.view.entrada.EntradaPage;
import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

/**
 *
 */
public abstract class PetServerInitializer implements WebApplicationInitializer {

    @Override
    public final void onStartup(ServletContext ctx) throws ServletException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(getSpringConfigurationClass());
        beforeSingularConfiguration(ctx, applicationContext);
        addSpringContextListener(ctx, applicationContext);
        addSpringRequestContextListener(ctx, applicationContext);
        addSpringMVCServlet(ctx, applicationContext);
        addSpringSecurityFilter(ctx, applicationContext);
        addWicketFilter(ctx, applicationContext);
        afterSingularConfiguration(ctx, applicationContext);
    }

    protected void beforeSingularConfiguration(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
    }

    protected void afterSingularConfiguration(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
    }

    protected void addSpringContextListener(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx.addListener(new ContextLoaderListener(applicationContext));
    }

    protected void addSpringRequestContextListener(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx.addListener(RequestContextListener.class);
    }

    protected void addSpringMVCServlet(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        //Servlet
        ServletRegistration.Dynamic dispatcher = ctx.addServlet("Spring MVC Dispatcher Servlet", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(getSpringMVServletMapping());
    }

    protected abstract Class<? extends PetServerSpringAppConfig> getSpringConfigurationClass();

    protected String getSpringMVServletMapping() {
        return "/*";
    }

    protected void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }

    protected void addWicketFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        String path = "/*";
        FilterRegistration.Dynamic wicketFilter = ctx.addFilter("PeticionamentoApplication", WicketFilter.class);
        wicketFilter.setInitParameter("applicationClassName", PetApplication.class.getName());
        wicketFilter.setInitParameter("homePageClass", EntradaPage.class.getName());
        wicketFilter.setInitParameter("filterMappingUrlPattern", path);
        wicketFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, path);
    }


}
