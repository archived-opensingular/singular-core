package org.opensingular.server.commons.spring.security;

import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.util.url.UrlToolkitBuilder;
import org.opensingular.server.commons.wicket.SingularApplication;
import org.opensingular.server.commons.wicket.SingularSession;

import javax.servlet.ServletContext;

public class SecurityAuthPathsFactory {

    public SecurityAuthPaths get() {

        SingularSession     singularSession     = SingularSession.get();
        SingularApplication singularApplication = SingularApplication.get();

        SingularUserDetails userDetails = singularSession.getUserDetails();
        IServerContext      serverContext = userDetails.getServerContext();

        ServletContext servletContext = singularApplication.getServletContext();

        UrlToolkitBuilder urlToolkitBuilder = new UrlToolkitBuilder();

        return new SecurityAuthPaths(servletContext.getContextPath(), serverContext.getUrlPath(), urlToolkitBuilder);
    }

}
