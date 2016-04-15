package br.net.mirante.singular.server.commons.config;

import br.net.mirante.singular.server.commons.wicket.SingularApplication;
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
        for (IServerContext context : getServerContexts()) {
            addWicketFilter(ctx, context);
        }
    }

    protected IServerContext[] getServerContexts(){
        return ServerContext.values();
    }


    protected void addWicketFilter(ServletContext ctx, IServerContext context) {
        FilterRegistration.Dynamic wicketFilterAnalise = ctx.addFilter(context.getClass().getName(), WicketFilter.class);
        wicketFilterAnalise.setInitParameter("applicationClassName", getWicketApplicationClass(context).getName());
        wicketFilterAnalise.setInitParameter("filterMappingUrlPattern", context.getContextPath());
        wicketFilterAnalise.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, context.getContextPath());
    }

    protected Class<? extends SingularApplication> getWicketApplicationClass(IServerContext context) {
        return SingularApplication.class;
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
