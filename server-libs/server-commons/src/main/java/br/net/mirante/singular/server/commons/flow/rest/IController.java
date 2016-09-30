/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import javax.inject.Inject;

import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.spring.security.AuthorizationService;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;

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
