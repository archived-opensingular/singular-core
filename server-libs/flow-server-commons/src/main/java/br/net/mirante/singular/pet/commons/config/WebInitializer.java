package br.net.mirante.singular.pet.commons.config;

import br.net.mirante.singular.pet.commons.spring.security.ServerContext;
import br.net.mirante.singular.pet.commons.wicket.PetApplication;
import org.apache.wicket.protocol.http.WicketFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.EnumSet;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class WebInitializer {


    public void init(ServletContext ctx) throws ServletException {
        onStartup(ctx);
    }

    protected void onStartup(ServletContext ctx) throws ServletException {
        addSessionListener(ctx);
        addWicketFilterPeticionamento(ctx);
        addWicketFilterAnalise(ctx);
    }

    protected void addWicketFilterAnalise(ServletContext ctx) {
        FilterRegistration.Dynamic wicketFilterAnalise = ctx.addFilter("AnaliseApplication", WicketFilter.class);
        wicketFilterAnalise.setInitParameter("applicationClassName", getWicketApplicationClassAnalise().getName());
        wicketFilterAnalise.setInitParameter("filterMappingUrlPattern", ServerContext.ANALISE.getContextPath());
        wicketFilterAnalise.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, ServerContext.ANALISE.getContextPath());
    }

    protected void addWicketFilterPeticionamento(ServletContext ctx) {
        FilterRegistration.Dynamic wicketFilterPeticionamento = ctx.addFilter("PeticionamentoApplication", WicketFilter.class);
        wicketFilterPeticionamento.setInitParameter("applicationClassName", getWicketApplicationClassPeticionamento().getName());
        wicketFilterPeticionamento.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, ServerContext.PETICIONAMENTO.getContextPath());
        wicketFilterPeticionamento.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, ServerContext.PETICIONAMENTO.getContextPath());
    }

    protected Class<? extends PetApplication> getWicketApplicationClassPeticionamento() {
        return PetApplication.class;
    }

    protected Class<? extends PetApplication> getWicketApplicationClassAnalise() {
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
