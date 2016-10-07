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

package org.opensingular.server.commons.flow.rest;

import javax.inject.Inject;

import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.spring.security.AuthorizationService;

public abstract class IController {

    @Inject
    private AuthorizationService authorizationService;

    public ActionResponse run(PetitionEntity petition, ActionRequest actionRequest) {
        if (hasPermission(petition, actionRequest)) {
            return execute(petition, actionRequest);
        } else {
            return new ActionResponse("Você não tem permissão para executar esta ação.", false);
        }
    }

    private boolean hasPermission(PetitionEntity petition, ActionRequest actionRequest) {
        if (getType() == Type.PROCESS) {
            return authorizationService.hasPermission(petition, null, actionRequest.getIdUsuario(), getActionName());
        } else {
            return authorizationService.hasPermission(petition, null, actionRequest.getIdUsuario(), getActionName());
        }
    }

    public abstract String getActionName();

    protected abstract ActionResponse execute(PetitionEntity petition, ActionRequest actionRequest);

    protected Type getType() {
        return Type.PROCESS;
    }

    protected enum Type {
        PROCESS, FORM
    }
}
