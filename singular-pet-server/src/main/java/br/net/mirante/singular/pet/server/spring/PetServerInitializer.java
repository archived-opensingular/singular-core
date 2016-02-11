package br.net.mirante.singular.pet.server.spring;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.EnumSet;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class PetServerInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext ctx) throws ServletException {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(getSpringConfigurationClass());
        applicationContext.register(getSpringSecurityConfigAnaliseClass());
        applicationContext.register(getSpringSecurityConfigPeticionamentoClass());
        onStartup(ctx, applicationContext);
    }

    public void onStartup(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) throws ServletException {
        addSessionListener(ctx);
        addSpringContextListener(ctx, applicationContext);
        addSpringRequestContextListener(ctx, applicationContext);
        addSpringMVCServlet(ctx, applicationContext);
        addSpringSecurityFilter(ctx, applicationContext);
        addWicketFilterPeticionamento(ctx, applicationContext);
        addWicketFilterAnalise(ctx, applicationContext);
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
     *
     * @return Uma classe concreta que herda de {@link PetServerSpringAppConfig} e anotada com {@link org.springframework.context.annotation.Configuration}
     */
    protected abstract Class<? extends PetServerSpringAppConfig> getSpringConfigurationClass();

    protected String getSpringMVServletMapping() {
        return "/*";
    }

    protected void addSpringSecurityFilter(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        ctx
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class)
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, getSpringMVServletMapping());
    }

    protected abstract <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigAnaliseClass();

    protected abstract <T extends WebSecurityConfigurerAdapter> Class<T> getSpringSecurityConfigPeticionamentoClass();


    protected void addWicketFilterAnalise(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {

        FilterRegistration.Dynamic wicketFilterAnalise = ctx.addFilter("AnaliseApplication", WicketFilter.class);
        wicketFilterAnalise.setInitParameter("applicationClassName", getWicketApplicationClass().getName());
        wicketFilterAnalise.setInitParameter("homePageClass", getHomePage());
        wicketFilterAnalise.setInitParameter("filterMappingUrlPattern", getPathAnalise());
        wicketFilterAnalise.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, getPathAnalise());
    }

    protected void addWicketFilterPeticionamento(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        FilterRegistration.Dynamic wicketFilterPeticionamento = ctx.addFilter("PeticionamentoApplication", WicketFilter.class);
        wicketFilterPeticionamento.setInitParameter("applicationClassName", getWicketApplicationClass().getName());
        wicketFilterPeticionamento.setInitParameter("homePageClass", getHomePage());
        wicketFilterPeticionamento.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, getPathPeticionamento());
        wicketFilterPeticionamento.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, getPathPeticionamento());
    }

    protected String getPathPeticionamento() {
        return DefaultConfigConstants.PETICIONAMENTO_CONTEXT;
    }

    protected String getPathAnalise() {
        return DefaultConfigConstants.ANALISE_CONTEXT;
    }

    protected abstract String getHomePage();

    protected Class<? extends PetApplication> getWicketApplicationClass() {
        return PetApplication.class;
    }

    /**
     * Configura o timeout da sessão web em minutos
     *
     * @return
     */
    protected int getSessionTimeoutMinutes() {
        return 30;
    }

    /**
     * Configura o session timeout da aplicação
     * Criado para permitir a remoção completa do web.xml
     *
     * @param servletContext
     */
    protected final void addSessionListener(ServletContext servletContext) {
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
