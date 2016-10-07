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
