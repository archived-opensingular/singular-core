package org.opensingular.singular.server.commons.wicket.listener;

import org.opensingular.singular.server.commons.config.IServerContext;
import org.opensingular.singular.server.commons.config.SingularServerConfiguration;
import org.opensingular.singular.server.commons.spring.security.SecurityUtil;
import org.opensingular.singular.server.commons.wicket.SingularApplication;
import org.opensingular.singular.server.commons.wicket.SingularSession;
import org.opensingular.singular.server.commons.wicket.error.Page500;
import org.opensingular.singular.util.wicket.page.error.Error403Page;
import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;

import javax.servlet.http.HttpServletRequest;

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
            IServerContext context = IServerContext.getContextFromRequest(request, singularServerConfiguration.getContexts());
            if (!SingularSession.get().getServerContext().equals(context)) {
                resetLogin(cycle);
            }
        }
    }

    private void resetLogin(RequestCycle cycle) {
        final Url url = cycle.getUrlRenderer().getBaseUrl();
        final String redirectURL = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + SecurityUtil.getLogoutPath();
        throw new RedirectToUrlException(redirectURL);
    }

    private void redirect403(RequestCycle cycle) {
        cycle.getOriginalResponse().reset();
        cycle.setResponsePage(new Error403Page());
    }

    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
//        return super.onException(cycle, ex);
        return new RenderPageRequestHandler(new PageProvider(new Page500(ex)));
    }

    private boolean isPageRequest(IRequestHandler handler) {
        return handler instanceof IPageClassRequestHandler;
    }

}
