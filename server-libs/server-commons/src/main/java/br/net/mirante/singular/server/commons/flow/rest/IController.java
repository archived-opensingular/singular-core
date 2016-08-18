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
    private PermissionResolverService permissionResolverService;

    public ActionResponse run(PetitionEntity petition, Action action) {
        boolean hasPermission = permissionResolverService.hasPermission(petition.getCod(), action.getIdUsuario(), getActionName());
        if (hasPermission) {
            return execute(petition, action);
        } else {
            return new ActionResponse("Você não tem permissão para executar esta ação.", false);
        }
    }

    public abstract ActionResponse execute(PetitionEntity petition, Action action);

    public abstract String getActionName();

    public boolean isExecutable() {
        return true;
    }
}
