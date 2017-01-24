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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * The type Cas 30 proxy receiving ticket validation filter wrapper.
 */
public class Cas30ProxyReceivingTicketValidationFilterWrapper extends SSOConfigurableFilter {

    private static final Logger logger = LoggerFactory.getLogger(Cas30ProxyReceivingTicketValidationFilterWrapper.class);

    /**
     * Constante DELEGATE_CLASS_NAME.
     */
    private static final String DELEGATE_CLASS_NAME = "org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter";

    /**
     * Constante CAS_SERVER_URL_PREFIX_PARAM.
     */
    private static final String CAS_SERVER_URL_PREFIX_PARAM = "casServerUrlPrefix";

    /**
     * Constante SERVER_NAME_PARAM.
     */
    private static final String SERVER_NAME_PARAM = "serverName";


    private static final String EXCEPTION_ON_TICKET_INVALID = "exceptionOnValidationFailure";


    /**
     * Campo delegate.
     */
    private Filter delegate = null;

        /**
     * Instantiates a new Cas 30 proxy receiving ticket validation filter wrapper.
     */
    public Cas30ProxyReceivingTicketValidationFilterWrapper() {
    }

    /**
     * Inicializa.
     *
     * @param filterConfig um filter config
     * @throws ServletException uma exceção servlet exception
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        final Map<String, String> params = new HashMap<String, String>();
        params.put(SERVER_NAME_PARAM, SingularProperties.get().getProperty(getSingularContext().getServerPropertyKey(SSOFilter.SSO_CLIENT_SERVER)));
        params.put(CAS_SERVER_URL_PREFIX_PARAM, SingularProperties.get().getProperty(getSingularContext().getServerPropertyKey(SSOFilter.SSO_URL_PREFIX)));
        params.put(EXCEPTION_ON_TICKET_INVALID, "true");
        Enumeration enumeration = filterConfig.getInitParameterNames();
        while (enumeration.hasMoreElements()) {
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
    }

    /**
     * Do filter.
     *
     * @param request  um request
     * @param response um response
     * @param chain    um chain
     * @throws IOException      Métodos subjeito a erros de entrada e saída.
     * @throws ServletException uma exceção servlet exception
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            delegate.doFilter(request, response, chain);
        } catch (ServletException exception) {
            logger.error(exception.getMessage(), exception);
            SSOLogoutUtil.logout((HttpServletRequest) request, (HttpServletResponse) response, getSingularContext());
        }
    }

    /**
     * Destroy.
     */
    @Override
    public void destroy() {
        delegate.destroy();
    }
}
