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
import org.opensingular.server.commons.util.url.UrlToolkit;
import org.opensingular.server.commons.util.url.UrlToolkitBuilder;

import java.io.Serializable;

public class SecurityAuthPaths implements Serializable {

    private final String            urlPath;
    private final String            contextPath;
    private final UrlToolkitBuilder urlToolkitBuilder;

    public SecurityAuthPaths(String contextPath, String urlPath,UrlToolkitBuilder urlToolkitBuilder) {
        this.urlPath = urlPath;
        this.contextPath = contextPath;
        this.urlToolkitBuilder = urlToolkitBuilder;
    }

    public String getLoginPath() {
        return contextPath + urlPath + "/login";
    }

    public String getLogoutPath(RequestCycle requestCycle) {
        String baseUrl = contextPath + urlPath + "/logout";
        if (requestCycle != null) {
            baseUrl = mountLogoutPathWithRequectCycle(requestCycle, baseUrl);
        }
        return baseUrl;
    }

    private String mountLogoutPathWithRequectCycle(RequestCycle requestCycle, String baseUrl) {
        Request    request    = requestCycle.getRequest();
        Url        url        = request.getUrl();
        UrlToolkit urlToolkit = urlToolkitBuilder.build(url);
        baseUrl = urlToolkit.concatServerAdressWithContext(baseUrl);
        baseUrl += "?service=" + urlToolkit.concatServerAdressWithContext(contextPath);
        return baseUrl;
    }

}