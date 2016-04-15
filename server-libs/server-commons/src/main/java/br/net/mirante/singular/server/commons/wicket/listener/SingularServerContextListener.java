package br.net.mirante.singular.server.commons.wicket.listener;

import javax.servlet.http.HttpServletRequest;

import br.net.mirante.singular.server.commons.wicket.PetSession;
import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;

import br.net.mirante.singular.server.commons.spring.security.SecurityUtil;
import br.net.mirante.singular.server.commons.spring.security.ServerContext;
import br.net.mirante.singular.util.wicket.page.error.Error403Page;

/**
 * Listener para impedir que páginas de um contexto do wicket sejam acessadas por uma sessão
 * criada em outro contexto  wicket.
 */
public class SingularServerContextListener extends AbstractRequestCycleListener {

    @Override
    public void onRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler) {
        if (PetSession.get().isAuthtenticated() && isPageRequest(handler)) {
            HttpServletRequest request = (HttpServletRequest) cycle.getRequest().getContainerRequest();
            ServerContext context = ServerContext.getContextFromRequest(request);
            if (!PetSession.get().getServerContext().equals(context)) {
                resetLogin(cycle);
            }
        }
    }

    private void resetLogin(RequestCycle cycle){
        final Url url = cycle.getUrlRenderer().getBaseUrl();
        final String redirectURL = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + SecurityUtil.getLogoutPath();
        throw new RedirectToUrlException(redirectURL);
    }

    private void redirect403(RequestCycle cycle) {
        cycle.getOriginalResponse().reset();
        cycle.setResponsePage(new Error403Page());
    }

    private boolean isPageRequest(IRequestHandler handler) {
        return handler instanceof IPageClassRequestHandler;
    }

}
