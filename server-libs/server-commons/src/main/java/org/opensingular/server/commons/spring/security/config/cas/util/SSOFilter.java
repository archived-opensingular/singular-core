package org.opensingular.server.commons.spring.security.config.cas.util;


import com.google.common.base.Strings;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.opensingular.server.commons.config.IServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro unificado para configuração do SSO
 * Parâmetros suportados:
 * <ul>
 * <li>urlExcludePattern [opcional]: String de urls abaixo do contexto da aplicação que não devem ser consideradas pelo filtro de autenticação.
 * </li>
 * <li>
 * propertiesFileName [obrigatorio se e somente se propertiesProviderClass não for definido]  Nome do arquivo de propriedades com as propriedades de configuração do SSO a serem utilizadas
 * </li>
 * <li>
 * logoutUrl [opcional, default=/logout]: url interceptada por este filtro para realizar o logout da aplicação.
 * </li>
 * </ul>
 */
public class SSOFilter extends SSOConfigurableFilter {

    /**
     * The constant SSO_CLIENT_SERVER.
     */
    public static final String SSO_CLIENT_SERVER = "cas.server.name";
    /**
     * The constant SSO_URL_PREFIX.
     */
    public static final String SSO_URL_PREFIX    = "cas.url.prefix";
    /**
     * The constant SSO_LOGOUT.
     */
    public static final String SSO_LOGOUT        = "cas.logout";
    /**
     * The constant SSO_LOGIN.
     */
    public static final String SSO_LOGIN         = "cas.login";



    private static final Logger logger = LoggerFactory.getLogger(SSOFilter.class);
    /**
     * Constante URL_EXCLUDE_PATTERN_PARAM.
     */
    private static final String URL_EXCLUDE_PATTERN_PARAM = "urlExcludePattern";

    private static final String CLIENT_LOGOUT_URL = "logoutUrl";
    /**
     * The Internal filter chain.
     */
    Filter[] internalFilterChain = new Filter[]{
            new SingleSingOutFilterWrapper(),
            new AuthenticationFilterWrapper(),
            new Cas30ProxyReceivingTicketValidationFilterWrapper(),
            new HttpServletRequestWrapperFilter()
    };
    /**
     * Campo para a url exclude pattern.
     */
    private String[] urlExcludePatterns = null;
    private String   logoutUrl          = "/logout";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        if (!Strings.isNullOrEmpty(filterConfig.getInitParameter(URL_EXCLUDE_PATTERN_PARAM))) {
            String urlExcludePattern = filterConfig.getInitParameter(URL_EXCLUDE_PATTERN_PARAM);
            urlExcludePatterns = urlExcludePattern.split(",");
        }
        if (!Strings.isNullOrEmpty(filterConfig.getInitParameter(CLIENT_LOGOUT_URL))) {
            logoutUrl = filterConfig.getInitParameter(CLIENT_LOGOUT_URL);
        }
        for (Filter f : internalFilterChain) {
            f.init(filterConfig);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (urlMatches(request, logoutUrl)) {
            SSOLogoutUtil.logout(request, (HttpServletResponse) response, getSingularContext());
        } else if (isURLExcluded(request)) {
            chain.doFilter(request, response);
        } else {
            new VirtualFilterChain(chain, internalFilterChain).doFilter(request, response);
        }
    }


    private boolean isURLExcluded(HttpServletRequest request) {
        if (urlExcludePatterns != null) {
            for (String urlExcludePattern : urlExcludePatterns) {
                if (urlMatches(request, urlExcludePattern)) {
                    logger.info(
                            String.format("Filter skipped due to regex patterns defined in urlExcludePattern properties ",
                                    request.getRequestURL()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean urlMatches(HttpServletRequest request, String path) {
        String url         = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replaceFirst(contextPath, "");
        if (url.matches(path)) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        for (Filter f : internalFilterChain) {
            f.destroy();
        }
    }


    private static class VirtualFilterChain implements FilterChain {
        private final FilterChain originalChain;
        private final List<? extends Filter> additionalFilters;
        private int currentPosition = 0;

        private VirtualFilterChain(FilterChain chain, Filter[] additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = Arrays.asList(additionalFilters);
        }

        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException,
                ServletException {
            if (currentPosition == additionalFilters.size()) {
                originalChain.doFilter(request, response);
            } else {
                currentPosition++;
                Filter nextFilter = additionalFilters.get(currentPosition - 1);
                nextFilter.doFilter(request, response, this);
            }
        }

    }
}
