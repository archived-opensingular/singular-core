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

package org.opensingular.server.commons.wicket.listener;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.http.WebRequest;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.wicket.util.page.error.Error403Page;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.config.SingularServerConfiguration;
import org.opensingular.server.commons.exception.SingularServerIntegrationException;
import org.opensingular.server.commons.spring.security.SecurityAuthPaths;
import org.opensingular.server.commons.spring.security.SecurityAuthPathsFactory;
import org.opensingular.server.commons.wicket.SingularApplication;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.error.Page410;
import org.opensingular.server.commons.wicket.error.Page500;

/**
 * Listener para impedir que páginas de um contexto do wicket sejam acessadas por uma sessão
 * criada em outro contexto  wicket.
 */
public class SingularServerContextListener extends AbstractRequestCycleListener {

    @Override
    public void onRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler) {
        SingularServerConfiguration singularServerConfiguration = SingularApplication.get().getApplicationContext().getBean(SingularServerConfiguration.class);
        if (SingularSession.get().isAuthtenticated() && isPageRequest(handler)) {
            HttpServletRequest request = (HttpServletRequest) cycle.getRequest().getContainerRequest();
            IServerContext     context = IServerContext.getContextFromRequest(request, singularServerConfiguration.getContexts());
            if (!SingularSession.get().getServerContext().equals(context)) {
                resetLogin(cycle);
            }
        }
    }

    private void resetLogin(RequestCycle cycle) {
        SecurityAuthPathsFactory securityAuthPathsFactory = new SecurityAuthPathsFactory();
        SecurityAuthPaths        securityAuthPaths        = securityAuthPathsFactory.get();
        throw new RedirectToUrlException(securityAuthPaths.getLogoutPath(cycle));
    }

    private void redirect403(RequestCycle cycle) {
        cycle.getOriginalResponse().reset();
        cycle.setResponsePage(new Error403Page());
    }

    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
        SingularException singularException = getFirstSingularException(ex);
        if (singularException instanceof SingularServerIntegrationException
                && ((WebRequest) RequestCycle.get().getRequest()).isAjax()) {
            return new AjaxErrorRequestHandler(singularException);
        } else if (ex instanceof PageExpiredException) {
            return new RenderPageRequestHandler(new PageProvider(new Page410()));
        } else {
            return new RenderPageRequestHandler(new PageProvider(new Page500(ex)));
        }
    }

    private SingularException getFirstSingularException(Exception ex) {
        for (Throwable t : ExceptionUtils.getThrowableList(ex)) {
            if (t instanceof SingularException) {
                return (SingularException) t;
            }
        }

        return null;
    }

    private boolean isPageRequest(IRequestHandler handler) {
        return handler instanceof IPageClassRequestHandler;
    }

}
