package br.net.mirante.singular.server.core.config;

import br.net.mirante.singular.server.commons.config.IServerContext;
import br.net.mirante.singular.server.commons.config.WebInitializer;
import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import br.net.mirante.singular.server.core.wicket.WorklistApplication;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * Configura os filtros, servlets e listeners default do singular pet server
 * e as configurações básicas do spring e spring-security
 */
public class WWebInitializer extends WebInitializer {

    @Override
    public void init(ServletContext ctx) throws ServletException {
        addOpenSessionInView(ctx);
        super.init(ctx);
    }

    @Override
    protected Class<? extends SingularApplication> getWicketApplicationClass(IServerContext context) {
        return WorklistApplication.class;
    }


    private void addOpenSessionInView(ServletContext servletContext) {
        FilterRegistration.Dynamic opensessioninview = servletContext.addFilter("opensessioninview", OpenSessionInViewFilter.class);
        opensessioninview.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, "/*");
        opensessioninview.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/SingularWS");
        opensessioninview.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }
}
