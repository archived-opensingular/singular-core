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

package org.opensingular.server.commons.wicket;

import static org.opensingular.server.commons.wicket.view.template.Menu.MENU_CACHE;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.springframework.security.core.context.SecurityContextHolder;

import org.opensingular.flow.persistence.entity.ProcessGroupEntity;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.spring.security.SingularUserDetails;
import org.opensingular.server.commons.wicket.view.template.MenuSessionConfig;

public class SingularSession extends AuthenticatedWebSession {

    private ProcessGroupEntity categoriaSelecionada;

    public SingularSession(Request request, Response response) {
        super(request);
    }

    public static SingularSession get() {
        return (SingularSession) Session.get();
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return true;
    }

    @Override
    public Roles getRoles() {
        if (getUserDetails() != null) {
            return new Roles(getUserDetails().getPermissions().stream().map( sp -> sp.getSingularId()).collect(Collectors.toList()).toArray(new String[0]));
        }
        return new Roles();
    }

    public List<String> getRoleIds() {
        return getRoles().stream().collect(Collectors.toList());
    }

    public String getName() {
        if (getUserDetails() != null) {
            return getUserDetails().getDisplayName();
        }
        return "";
    }

    public String getUsername() {
        if (getUserDetails() != null) {
            return getUserDetails().getUsername();
        }
        return "";
    }

    public boolean isAuthtenticated() {
        return getUserDetails() != null;
    }

    /**
     * @return O contexto atual da sessão ou null caso ainda não tenha sido definido.
     */
    public IServerContext getServerContext() {
        if (isAuthtenticated()) {
            return getUserDetails().getServerContext();
        }
        return null;
    }


    public <T extends SingularUserDetails> T getUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SingularUserDetails) {
            return (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    public ProcessGroupEntity getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void setCategoriaSelecionada(ProcessGroupEntity categoriaSelecionada) {
        this.categoriaSelecionada = categoriaSelecionada;
    }

    public MenuSessionConfig getMenuSessionConfig() {
        return (MenuSessionConfig) getAttribute(MENU_CACHE);
    }

}

