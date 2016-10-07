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

package org.opensingular.server.commons.spring.security.config.cas;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.server.commons.spring.security.config.SingularLogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

public class SingularCASLogoutHandler implements SingularLogoutHandler, Loggable {

    private String logoutURL;

    public SingularCASLogoutHandler() {
    }

    public SingularCASLogoutHandler(String logoutURL) {
        this.logoutURL = logoutURL;
    }

    @Override
    public void handleLogout(HttpServletRequest req, HttpServletResponse resp) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        try {
            getLogger().warn(" CAPTURADA REQUEST DE LOGOUT EM : " + request.getRequestURI() + ". A SESSAO DESSA APLICACAO SERA INVALIDADA E SERA FEITO O SINGLE SIGN OUT");
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            String redirect = logoutURL + "?service=" + URLEncoder.encode(extractServiceParam(request), "UTF-8");
            getLogger().warn(" REDIRECIONANDO PARA: " + redirect);
            response.sendRedirect(redirect);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    private String extractServiceParam(HttpServletRequest request) {
        String service = request.getParameter("service");
        if (service == null || service.length() < 1) {
            String url = request.getRequestURL().toString();
            int index = url.lastIndexOf(request.getContextPath());
            service = url.substring(0, index) + request.getContextPath();
        }
        return service;

    }
}
