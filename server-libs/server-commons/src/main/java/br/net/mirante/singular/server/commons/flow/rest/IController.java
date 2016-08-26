/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import javax.inject.Inject;

import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;

public abstract class IController {

    @Inject
    private PermissionResolverService     permissionResolverService;

    public ActionResponse run(PetitionEntity petition, Action action) {
        if (hasPermission(petition, action)) {
            return execute(petition, action);
        } else {
            return new ActionResponse("Você não tem permissão para executar esta ação.", false);
        }
    }

    private boolean hasPermission(PetitionEntity petition, Action action) {
        if (getType() == Type.PROCESS) {
            return permissionResolverService.hasFlowPermission(petition, action.getIdUsuario(), getActionName());
        } else {
            return permissionResolverService.hasFormPermission(petition, action.getIdUsuario(), getActionName());
        }
    }

    public abstract String getActionName();

    protected abstract ActionResponse execute(PetitionEntity petition, Action action);

    protected Type getType() {
        return Type.PROCESS;
    }

    protected enum Type {
        PROCESS, FORM
    }
}
