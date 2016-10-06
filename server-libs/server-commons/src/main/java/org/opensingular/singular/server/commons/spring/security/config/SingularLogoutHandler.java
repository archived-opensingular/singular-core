package org.opensingular.singular.server.commons.spring.security.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SingularLogoutHandler {

    public void handleLogout(HttpServletRequest req, HttpServletResponse resp);
}
