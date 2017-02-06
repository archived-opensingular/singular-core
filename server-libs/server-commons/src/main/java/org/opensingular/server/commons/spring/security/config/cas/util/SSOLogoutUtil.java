package org.opensingular.server.commons.spring.security.config.cas.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.exception.SingularServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The type Sso logout util.
 */
public class SSOLogoutUtil {

    private static final Logger logger = LoggerFactory.getLogger(SSOLogoutUtil.class);

    /**
     * Logout.
     *
     * @param request  the request
     * @param response the response
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response, IServerContext context) {
        try {
            logger.warn(" CAPTURADA REQUEST DE LOGOUT EM : {}. A SESSAO DESSA APLICACAO SERA INVALIDADA E SERA FEITO O SINGLE SIGN OUT",
                    StringEscapeUtils.escapeJava(request.getRequestURI()));
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            String redirect = SingularProperties.get().getProperty(context.getServerPropertyKey(SSOFilter.SSO_LOGOUT)) + "?service=" + URLEncoder.encode(extractServiceParam(request), StandardCharsets.UTF_8.name());
            logger.warn(" REDIRECIONANDO PARA: {}", StringEscapeUtils.escapeJava(redirect));
            response.sendRedirect(redirect);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw SingularServerException.rethrow(e.getMessage(), e);
        }
    }


    private static String extractServiceParam(HttpServletRequest request) {
        String service = request.getParameter("service");
        if (service == null || service.length() < 1) {
            String url = request.getRequestURL().toString();
            int index = url.lastIndexOf(request.getContextPath());
            service = url.substring(0, index) + request.getContextPath();
        }
        return service;
    }
}
