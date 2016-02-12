package br.net.mirante.singular.pet.module.wicket.listener;

import br.net.mirante.singular.pet.module.spring.security.ServerContext;
import br.net.mirante.singular.pet.module.wicket.PetSession;
import br.net.mirante.singular.util.wicket.page.error.Error403Page;
import org.apache.wicket.core.request.handler.IPageClassRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.servlet.http.HttpServletRequest;

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
                redirect403(cycle);
            }
        }
    }

    private void redirect403(RequestCycle cycle) {
        cycle.getOriginalResponse().reset();
        cycle.setResponsePage(new Error403Page());
    }

    private boolean isPageRequest(IRequestHandler handler) {
        return handler instanceof IPageClassRequestHandler;
    }

}
