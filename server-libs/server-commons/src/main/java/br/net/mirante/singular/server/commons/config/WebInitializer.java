package br.net.mirante.singular.server.commons.config;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.wicket.protocol.http.WicketFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

import br.net.mirante.singular.server.commons.wicket.SingularApplication;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public abstract class WebInitializer {


    static final String SINGULAR_SECURITY = "[SINGULAR][WEB] %s";
    public static final Logger logger = LoggerFactory.getLogger(SpringSecurityInitializer.class);

    public void init(ServletContext ctx) throws ServletException {
        onStartup(ctx);
    }

    protected void onStartup(ServletContext ctx) throws ServletException {
        addSessionListener(ctx);
        addOpenSessionInView(ctx);
        for (IServerContext context : serverContexts()) {
            logger.info(String.format(SINGULAR_SECURITY, "Setting up web context: "+context.getContextPath()));
            addWicketFilter(ctx, context);
        }
    }

    protected IServerContext[] serverContexts(){
        return ServerContext.values();
    }


    protected void addWicketFilter(ServletContext ctx, IServerContext context) {
        FilterRegistration.Dynamic wicketFilterAnalise = ctx.addFilter(context.getName()+System.identityHashCode(context), WicketFilter.class);
        wicketFilterAnalise.setInitParameter("applicationClassName", getWicketApplicationClass(context).getName());
        wicketFilterAnalise.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, context.getContextPath());
        wicketFilterAnalise.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, context.getContextPath());
    }

    protected abstract Class<? extends SingularApplication> getWicketApplicationClass(IServerContext context);

    private void addOpenSessionInView(ServletContext servletContext) {
        FilterRegistration.Dynamic opensessioninview = servletContext.addFilter("opensessioninview", OpenSessionInViewFilter.class);
        opensessioninview.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }

    protected String[] getDefaultPublicUrls() {
        List<String> urls = new ArrayList<>();
        urls.add("/rest/*");
        urls.add("/resources/*");
        urls.add("/index.html");
        for (IServerContext ctx : serverContexts()){
            urls.add(ctx.getUrlPath() + "/wicket/resource/*");
            urls.add(ctx.getUrlPath() + "/public/*");
        }
        return urls.toArray(new String[urls.size()]);
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
