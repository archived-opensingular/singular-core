/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.spring.security;


import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.server.commons.spring.security.config.cas.util.UrlToolkit;
import org.opensingular.server.commons.wicket.SingularApplication;
import org.opensingular.server.commons.wicket.SingularSession;

public class SecurityUtil {

    private final SingularSession singularSession;
    private final SingularApplication singularApplication;

    private SecurityUtil(SingularSession singularSession, SingularApplication singularApplication) {
        this.singularSession = singularSession;
        this.singularApplication = singularApplication;
    }

    public String getLoginPath() {
        return getContextPath() + getUrlPath() + "/login";
    }

    public String getLogoutPath(RequestCycle requestCycle) {

        String contextPath = getContextPath();
        String basicUrl    = contextPath + getUrlPath() + "/logout";

        if (requestCycle != null) {
            basicUrl = mountLogoutPathWithRequectCycle(requestCycle, contextPath, basicUrl);
        }

        return basicUrl;
    }

    private String mountLogoutPathWithRequectCycle(RequestCycle requestCycle, String contextPath, String basicUrl) {
        Request    request    = requestCycle.getRequest();
        Url        url        = request.getUrl();
        UrlToolkit urlToolkit = new UrlToolkit(url);
        basicUrl = urlToolkit.concatServerAdressWithContext(basicUrl);
        basicUrl += "?service=" + urlToolkit.concatServerAdressWithContext(contextPath);
        return basicUrl;
    }

    private String getUrlPath() {
        return singularSession.getUserDetails().getServerContext().getUrlPath();
    }

    private  String getContextPath() {
        return singularApplication.getServletContext().getContextPath();
    }

}