package org.opensingular.server.commons.spring.security.config.cas.util;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.opensingular.server.commons.config.IServerContext;

/**
 * The type Sso configurable filter.
 */
public abstract class SSOConfigurableFilter implements Filter {

    public static final String SINGULAR_CONTEXT_ATTRIBUTE      = "SSOFilterSingularContextAttribute";
    private IServerContext serverContext;

    protected IServerContext getSingularContext(){
        return serverContext;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.serverContext = (IServerContext) filterConfig.getServletContext().getAttribute(filterConfig.getInitParameter(SINGULAR_CONTEXT_ATTRIBUTE));
    }

    /**
     * Instantiates a new Sso configurable filter.
     */
    public SSOConfigurableFilter() {
    }
}
