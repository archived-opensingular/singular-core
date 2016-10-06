package org.opensingular.server.commons.spring.security;


import org.opensingular.server.commons.wicket.SingularApplication;
import org.opensingular.server.commons.wicket.SingularSession;

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
