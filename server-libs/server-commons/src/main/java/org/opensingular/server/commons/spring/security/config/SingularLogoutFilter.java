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

package org.opensingular.server.commons.spring.security.config;

import org.opensingular.lib.commons.util.Loggable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SingularLogoutFilter implements Filter, Loggable {

    private FilterConfig filterConfig;

    public SingularLogoutFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            SingularLogoutHandler singularLogoutHandler = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(SingularLogoutHandler.class);
            singularLogoutHandler.handleLogout((HttpServletRequest) req, (HttpServletResponse) resp);
        } catch (NoSuchBeanDefinitionException e) {
            getLogger().info("Não há  bean "+SingularLogoutHandler.class.getSimpleName()+" disponível no cotexto ignorando singular logout ");
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
