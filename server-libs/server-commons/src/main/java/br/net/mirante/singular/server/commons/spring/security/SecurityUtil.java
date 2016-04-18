package br.net.mirante.singular.server.commons.spring.security;


import br.net.mirante.singular.server.commons.wicket.SingularApplication;
import br.net.mirante.singular.server.commons.wicket.SingularSession;

public class SecurityUtil {

    private SecurityUtil() {

    }

    public static String getLoginPath() {
        return SingularApplication.get().getServletContext().getContextPath() +
                SingularSession.get().getUserDetails().getServerContext().getUrlPath() +
                "/login";
    }

    public static String getLogoutPath() {
        return SingularApplication.get().getServletContext().getContextPath() +
                SingularSession.get().getUserDetails().getServerContext().getUrlPath() +
                "/logout";
    }
}
