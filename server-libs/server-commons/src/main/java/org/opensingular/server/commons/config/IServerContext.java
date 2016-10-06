package org.opensingular.server.commons.config;

import org.opensingular.server.commons.exception.SingularServerException;
import org.apache.wicket.request.Request;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitário para prover a configuração de contexto atual e os métodos utilitários
 * relacionados.
 */
public interface IServerContext {

    public static IServerContext getContextFromRequest(Request request, IServerContext[] contexts) {
        return getContextFromRequest((HttpServletRequest) request.getContainerRequest(), contexts);
    }

    public static IServerContext getContextFromRequest(HttpServletRequest request, IServerContext[] contexts) {
        String contextPath = request.getContextPath();
        String context = request.getPathInfo().replaceFirst(contextPath, "");
        for (IServerContext ctx : contexts) {
            if (context.startsWith(ctx.getUrlPath())) {
                return ctx;
            }
        }
        throw new SingularServerException("Não foi possível determinar o contexto do servidor do singular");
    }

    /**
     * O contexto no formato aceito por servlets e filtros
     *
     * @return
     */
    public String getContextPath();

    /**
     * Conversao do formato aceito por servlets e filtros (contextPath) para java regex
     *
     * @return
     */
    public String getPathRegex();

    /**
     * Conversao do formato aceito por servlets e filtros (contextPath) para um formato de url
     * sem a / ao final.
     *
     * @return
     */
    public String getUrlPath();

    public String getPropertiesBaseKey();

    public String getName();

}
