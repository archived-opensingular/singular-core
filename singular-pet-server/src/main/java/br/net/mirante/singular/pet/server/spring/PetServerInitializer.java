package br.net.mirante.singular.pet.server.spring;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import br.net.mirante.singular.pet.module.wicket.PetApplication;

/**
 *Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class PetServerInitializer implements WebApplicationInitializer {

    @Override
    public final void onStartup(ServletContext ctx) throws ServletException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(getSpringConfigurationClass());
        beforeSingularConfiguration(ctx, applicationContext);
        addSessionListener(ctx);
        addSpringContextListener(ctx, applicationContext);
        addSpringRequestContextListener(ctx, applicationContext);
        addSpringMVCServlet(ctx, applicationContext);
        addSpringSecurityFilter(ctx, applicationContext);
        addWicketFilter(ctx, applicationContext);
        afterSingularConfiguration(ctx, applicationContext);
    }

    /**
     * Método destinado a ser sobrescrito para incluir qualquer configuração da aplicação web
     * antes que as configurações do singular pet server sejam feitas.
     *
     * @param ctx
     *  Servlet Context a partir do qual as configurações devem ser feitas
     * @param applicationContext
     *  Application context do spring.
     */
    protected void beforeSingularConfiguration(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
    }


    /**
     * Método destinado a ser sobrescrito para incluir qualquer configuração da aplicação web
     * após as configurações do singular pet server.
     *
     * @param ctx
     *  Servlet Context a partir do qual as configurações devem ser feitas
     * @param applicationContext
     *  Application context do spring.
     */
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

    /**
     * Fornece a classe que será utilizada como configuração java do Spring.
     * A classe fornecida deve herdar de {@link PetServerSpringAppConfig} e deve
     * ser anotada com {@link org.springframework.context.annotation.Configuration}.
     * As principais configurações do pet server são feitas pela superclasse bastando declarar
     * na classe informada apenas as configurações e beans do spring específicos da aplicação
     * @return
     *  Uma classe concreta que herda de {@link PetServerSpringAppConfig} e anotada com {@link org.springframework.context.annotation.Configuration}
     */
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
//        wicketFilter.setInitParameter("homePageClass", EntradaPage.class.getName());
        wicketFilter.setInitParameter("filterMappingUrlPattern", path);
        wicketFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, path);
    }

    /**
     * Configura o timeout da sessão web em minutos
     * @return
     */
    protected int getSessionTimeoutMinutes() {
        return 30;
    }

    /**
     * Configura o session timeout da aplicação
     * Criado para permitir a remoção completa do web.xml
     * @param servletContext
     */
    private void addSessionListener(ServletContext servletContext) {
        servletContext.addListener(new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                se.getSession().setMaxInactiveInterval(60 * getSessionTimeoutMinutes());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
            }
        });
    }


}
