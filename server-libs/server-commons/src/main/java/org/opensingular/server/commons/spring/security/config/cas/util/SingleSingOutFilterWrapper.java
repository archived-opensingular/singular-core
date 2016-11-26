package org.opensingular.server.commons.spring.security.config.cas.util;

import org.opensingular.lib.commons.base.SingularProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Single sing out filter wrapper.
 */
public class SingleSingOutFilterWrapper extends SSOConfigurableFilter {

    private static final Logger logger = LoggerFactory.getLogger(SingleSingOutFilterWrapper.class);

    /**
     * Constante DELEGATE_CLASS_NAME.
     */
    private static final String DELEGATE_CLASS_NAME = "org.jasig.cas.client.session.SingleSignOutFilter";

    /**
     * Constante CAS_SERVER_URL_PREFIX_PARAM.
     */
    private static final String CAS_SERVER_URL_PREFIX_PARAM = "casServerUrlPrefix";

    /**
     * Campo delegate.
     */
    private Filter delegate = null;
    /**
     * Instantiates a new Single sing out filter wrapper.
     */
    public SingleSingOutFilterWrapper() {
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        final Map<String, String> params = new HashMap<String, String>();
        params.put(CAS_SERVER_URL_PREFIX_PARAM, SingularProperties.get().getProperty(getSingularContext().getServerPropertyKey(SSOFilter.SSO_URL_PREFIX)));
        Enumeration enumeration = filterConfig.getInitParameterNames();
        for (; enumeration.hasMoreElements(); ) {
            String s = (String) enumeration.nextElement();
            params.put(s, filterConfig.getInitParameter(s));
        }
        try {
            FilterConfig newConfig = new FilterConfig() {

                @Override
                public ServletContext getServletContext() {
                    return filterConfig.getServletContext();
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return Collections.enumeration(params.keySet());
                }

                @Override
                public String getInitParameter(String name) {
                    return params.get(name);
                }

                @Override
                public String getFilterName() {
                    return filterConfig.getFilterName();
                }
            };
            delegate = (Filter) Class.forName(DELEGATE_CLASS_NAME).newInstance();
            delegate.init(newConfig);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
        delegate.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        delegate.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }
}
